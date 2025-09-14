package com.soyeon.sharedcalendar.calendar.app;

import com.soyeon.sharedcalendar.calendar.domain.*;
import com.soyeon.sharedcalendar.calendar.domain.repository.CalendarRepository;
import com.soyeon.sharedcalendar.calendar.dto.request.CalendarRequest;
import com.soyeon.sharedcalendar.calendar.dto.response.CalendarListResponse;
import com.soyeon.sharedcalendar.calendar.dto.response.CalendarResponse;
import com.soyeon.sharedcalendar.calendar.exception.calendar.CalendarUnauthorized;
import com.soyeon.sharedcalendar.common.img.app.ImgService;
import com.soyeon.sharedcalendar.common.security.SecurityUtils;
import com.soyeon.sharedcalendar.common.validator.ValidatorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.soyeon.sharedcalendar.calendar.domain.CalendarAccessLevel.*;
import static com.soyeon.sharedcalendar.calendar.utils.CalendarUtils.*;
import static com.soyeon.sharedcalendar.common.security.SecurityUtils.getCurrentMemberId;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CalendarService {
    private final ValidatorService validatorService;
    private final CalendarRepository calendarRepository;
    private final CalendarEventService calendarEventService;
    private final CalendarProfileImgService calendarProfileImgService;
    private final ImgService imgService;
    private final CalendarMemberService calendarMemberService;

    /**
     * 새 캘린더를 생성한다
     * @param request
     * @return
     */
    @Transactional
    public Calendar createCalendar(CalendarRequest request) {
        Long memberId = getCurrentMemberId();
        Calendar calendar = Calendar.create(memberId,
                request.calendarName(),
                request.accessLevel() == null ? READ_ONLY : request.accessLevel());
        return calendarRepository.save(calendar);
    }

    /**
     * 캘린더 프로필 이미지를 변경한다.
     * @param calendar
     * @param profileImgKey
     */
    @Transactional
    public void changeProfileImg(Calendar calendar, String profileImgKey) {
        calendar.changeProfileImg(profileImgKey);
        calendarRepository.updateProfileImgKey(calendar.getCalendarId(), profileImgKey);
    }

    /**
     * 공유 중인 캘린더 목록을 조회한다.
     */
    public List<CalendarListResponse> getCalendarList() {
        Long memberId = SecurityUtils.getCurrentMemberId();
        List<Calendar> calendars = calendarRepository.findAllCalendarsByMemberId(memberId);
        List<CalendarListResponse> list = new ArrayList<>();
        for (Calendar c : calendars) {
            String presignedUrl = imgService.getPresignedUrlByObjectKey(c.getProfileImgKey());
            list.add(CalendarListResponse
                    .create(c.getCalendarId(),
                            c.getCalendarName(),
                            presignedUrl)
                    );
        }
        return list;
    }

    /**
     * 캘린더를 삭제한다.
     * @param calendarId
     */
    @Transactional
    public void deleteCalendar(Long calendarId) {
        Calendar calendar = validatorService.validateCalendar(calendarId);
        if (!isOwner(calendar)) {
            throw new CalendarUnauthorized(calendar.getCalendarId());
        }
        calendarRepository.deleteById(calendarId);
    }

    /**
     * 캘린더를 수정한다.
     * @param calendarId
     * @param request
     * @return
     */
    @Transactional
    public CalendarResponse updateCalendar(Long calendarId, CalendarRequest request) {
        Long memberId = getCurrentMemberId();
        Calendar calendar = validatorService.validateCalendar(calendarId);
        boolean isOwner = isOwner(calendar);
        if (isOwner) {
            if (request.calendarName() != null && !request.calendarName().isBlank()) {
                calendar.changeCalendarName(request.calendarName());
            }
            if (request.accessLevel() != null) {
                calendar.changeDefaultAccessLevel(request.accessLevel());
            }
            if (request.imgMeta() != null) {
                CalendarImgMeta meta = calendarProfileImgService.createMetaForUpload(calendar.getCalendarId(), request.imgMeta());
                calendarProfileImgService.save(meta);

                // 캘린더에 이미지 update
                changeProfileImg(calendar, meta.getObjectKey());
            }
            calendarRepository.update(calendar);
        }
        CalendarAccessLevel myAccessLevel = calendarMemberService.getAccessLevel(calendar.getCalendarId(), memberId);
        List<CalendarEvent> events = calendarEventService.getEvents(
                calendarId,
                getDefaultStartDate(),
                getDefaultEndDate());
        return CalendarResponse.of(calendar, myAccessLevel, isOwner, events);
    }

    /**
     * 캘린더를 조회한다. (상세 일정 포함)
     * @param calendarId
     * @return
     */
    public CalendarResponse getCalendar(Long calendarId, LocalDateTime from, LocalDateTime to) {
        Long memberId = getCurrentMemberId();
        Calendar c = validatorService.validateCalendar(calendarId);
        CalendarAccessLevel myAccessLevel = calendarMemberService.getAccessLevel(c.getCalendarId(), memberId);
        if (from == null) {
            from = getDefaultStartDate();
        }
        if (to == null) {
            to = getDefaultEndDate();
        }
        List<CalendarEvent> events = calendarEventService.getEvents(calendarId, from, to);
        return CalendarResponse.of(c, myAccessLevel, isOwner(c), events);
    }

    /**
     * 요청한 회원이 캘린더 생성자인지 확인한다.
     * @param calendar
     */
    private boolean isOwner(Calendar calendar) {
        return calendar.getOwnerId().equals(getCurrentMemberId());
    }
}
