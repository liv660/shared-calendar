package com.soyeon.sharedcalendar.calendar.app;

import com.soyeon.sharedcalendar.calendar.domain.Calendar;
import com.soyeon.sharedcalendar.calendar.domain.CalendarEvent;
import com.soyeon.sharedcalendar.calendar.domain.repository.CalendarEventRepository;
import com.soyeon.sharedcalendar.calendar.domain.repository.CalendarRepository;
import com.soyeon.sharedcalendar.calendar.dto.request.CalendarRequest;
import com.soyeon.sharedcalendar.calendar.dto.response.CalendarListResponse;
import com.soyeon.sharedcalendar.calendar.dto.response.CalendarResponse;
import com.soyeon.sharedcalendar.common.img.app.ImgUploadService;
import com.soyeon.sharedcalendar.common.security.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.soyeon.sharedcalendar.calendar.domain.CalendarAccessLevel.*;
import static com.soyeon.sharedcalendar.calendar.utils.CalendarUtils.*;
import static com.soyeon.sharedcalendar.common.security.SecurityUtils.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CalendarService {
    private final ImgUploadService imgUploadService;
    private final CalendarRepository calendarRepository;
    private final CalendarEventRepository calendarEventRepository;

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
            log.info("[list] calendarId: {}", c.getCalendarId());
            list.add(CalendarListResponse
                    .create(c.getCalendarId(),
                            c.getCalendarName(),
                            c.getProfileImgKey()));
        }
        return list;
    }

    /**
     * 캘린더를 삭제한다.
     * @param calendarId
     */
    @Transactional
    public void deleteCalendar(Long calendarId) {
        Calendar calendar = isValidCalendar(calendarId);
        if (isOwner(calendar)) {
            calendarRepository.deleteById(calendarId);
        }
    }

    /**
     * 캘린더를 수정한다.
     * @param calendarId
     * @param request
     * @return
     */
    @Transactional
    public CalendarResponse updateCalendar(Long calendarId, CalendarRequest request) {
        Calendar calendar = isValidCalendar(calendarId);
        if (isOwner(calendar)) {
            if (request.calendarName() != null && !request.calendarName().isBlank()) {
                calendar.changeCalendarName(request.calendarName());
            }
            if (request.accessLevel() != null) {
                calendar.changeDefaultAccessLevel(request.accessLevel());
            }
            //TODO 이미지 처리
            calendarRepository.update(calendar);
        }

        List<CalendarEvent> events = calendarEventRepository.findReadable(
                calendarId,
                getCurrentMemberId(),
                getDefaultStartDate(),
                getDefaultEndDate());
        return CalendarResponse.of(calendar, events);
    }

    /**
     * 캘린더를 조회한다. (상세 일정 포함)
     * @param calendarId
     * @return
     */
    public CalendarResponse getCalendar(Long calendarId, LocalDateTime from, LocalDateTime to) {
        Calendar calendar = isValidCalendar(calendarId);
        if (from == null) {
            from = getDefaultStartDate();
        }
        if (to == null) {
            to = getDefaultEndDate();
        }
        Long memberId = getCurrentMemberId();
        List<CalendarEvent> events = calendarEventRepository.findReadable(calendarId, memberId, from, to);
        return CalendarResponse.of(calendar, events);
    }

    /**
     * 캘린더가 존재하는지 확인한다.
     * @param calendarId
     * @return
     */
    private Calendar isValidCalendar(Long calendarId) {
        return calendarRepository.findById(calendarId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "캘린더가 존재하지 않습니다."));
    }

    /**
     * 요청한 회원이 캘린더 생성자인지 확인한다.
     * @param calendar
     */
    private boolean isOwner(Calendar calendar) {
        Long memberId = getCurrentMemberId();
        if (!calendar.getOwnerId().equals(memberId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "캘린더 관리자만 할 수 있습니다.");
        }
        return true;
    }
}
