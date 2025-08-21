package com.soyeon.sharedcalendar.calendar.app;

import com.soyeon.sharedcalendar.calendar.domain.Calendar;
import com.soyeon.sharedcalendar.calendar.domain.repository.CalendarRepository;
import com.soyeon.sharedcalendar.calendar.dto.CalendarRequest;
import com.soyeon.sharedcalendar.calendar.dto.CalendarResponse;
import com.soyeon.sharedcalendar.common.id.SnowflakeIdGenerator;
import com.soyeon.sharedcalendar.common.security.SecurityUtils;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CalendarService {
    private final SnowflakeIdGenerator idGenerator;
    private final CalendarRepository calendarRepository;

    @Value("${profile.default-calendar}")
    private String defaultProfileImgUrl;

    /**
     * 새 캘린더를 생성한다
     * @param request
     * @return
     */
    @Transactional
    public CalendarResponse createCalendar(CalendarRequest request) {
        Long memberId = SecurityUtils.getCurrentMemberId();
        Long calendarId = idGenerator.nextId();

        String profileImgUrl = request.profileImgUrl();
        if (profileImgUrl == null || profileImgUrl.isEmpty()) {
            profileImgUrl = defaultProfileImgUrl;
        }

        Calendar calendar = Calendar.create(calendarId,
                memberId,
                request.calendarName(),
                request.accessLevel(),
                profileImgUrl);
        calendarRepository.save(calendar);

        return CalendarResponse.create(calendarId,
                memberId,
                request.calendarName(),
                request.accessLevel(),
                profileImgUrl);
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
            if (request.profileImgUrl() != null && !request.profileImgUrl().isBlank()) {
                calendar.changeProfileImgUrl(request.profileImgUrl());
            }
            calendarRepository.update(calendar);
        }
        return CalendarResponse.from(calendar, new ArrayList<>());
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
        Long memberId = SecurityUtils.getCurrentMemberId();
        if (!calendar.getOwnerId().equals(memberId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "캘린더 관리자만 할 수 있습니다.");
        }
        return true;
    }
}
