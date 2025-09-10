package com.soyeon.sharedcalendar.calendar.app;

import com.soyeon.sharedcalendar.calendar.domain.CalendarImgMeta;
import com.soyeon.sharedcalendar.calendar.domain.repository.CalendarImgMetaRepository;
import com.soyeon.sharedcalendar.calendar.dto.request.CalendarImgRequest;
import com.soyeon.sharedcalendar.common.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CalendarProfileImgService {
    private final CalendarImgMetaRepository calendarImgMetaRepository;

    /**
     * 캘린더 이미지 메타를 생성한다.
     * @param calendarId
     * @param req
     * @return
     */
    public CalendarImgMeta createMetaForUpload(Long calendarId, CalendarImgRequest req) {
        Long memberId = SecurityUtils.getCurrentMemberId();
        return CalendarImgMeta.create(memberId, calendarId, req.objectKey(),
                req.contentType(), req.bytes(), req.width(), req.height(), req.contentHash(), req.originalFilename());
    }

    /**
     * 캘린더 이미지 메타를 DB에 저장한다.
     * @param meta
     */
    public void save(CalendarImgMeta meta) {
        calendarImgMetaRepository.save(meta);
    }
}
