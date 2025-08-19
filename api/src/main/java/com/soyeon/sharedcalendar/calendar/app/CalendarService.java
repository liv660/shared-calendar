package com.soyeon.sharedcalendar.calendar.app;

import com.soyeon.sharedcalendar.calendar.domain.Calendar;
import com.soyeon.sharedcalendar.calendar.domain.repository.CalendarRepository;
import com.soyeon.sharedcalendar.calendar.dto.CalendarRequest;
import com.soyeon.sharedcalendar.calendar.dto.CalendarResponse;
import com.soyeon.sharedcalendar.common.id.SnowflakeIdGenerator;
import com.soyeon.sharedcalendar.common.security.SecurityUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
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

    @Transactional
    public void deleteCalendar(Long calendarId) {
        Long memberId = SecurityUtils.getCurrentMemberId();
        Calendar calendar = calendarRepository.findById(calendarId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "캘린더가 존재하지 않습니다."));

        if (!calendar.getOwnerId().equals(memberId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "캘린더 관리자만 삭제할 수 있습니다.");
        }
        calendarRepository.deleteById(calendarId);
    }
}
