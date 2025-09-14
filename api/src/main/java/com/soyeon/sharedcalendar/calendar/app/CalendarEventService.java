package com.soyeon.sharedcalendar.calendar.app;

import com.soyeon.sharedcalendar.calendar.domain.CalendarCategory;
import com.soyeon.sharedcalendar.calendar.domain.CalendarEvent;
import com.soyeon.sharedcalendar.calendar.domain.repository.CalendarCategoryRepository;
import com.soyeon.sharedcalendar.calendar.domain.repository.CalendarEventRepository;
import com.soyeon.sharedcalendar.calendar.dto.request.CalendarEventRequest;
import com.soyeon.sharedcalendar.calendar.dto.response.CalendarCategoriesResponse;
import com.soyeon.sharedcalendar.calendar.dto.response.CalendarEventDetailResponse;
import com.soyeon.sharedcalendar.calendar.exception.event.EventNotFound;
import com.soyeon.sharedcalendar.calendar.exception.event.EventUnauthorized;
import com.soyeon.sharedcalendar.common.img.app.ImgService;
import com.soyeon.sharedcalendar.common.validator.ValidatorService;
import com.soyeon.sharedcalendar.member.domain.Member;
import com.soyeon.sharedcalendar.member.domain.repository.MemberRepository;
import com.soyeon.sharedcalendar.member.dto.MemberDto;
import com.soyeon.sharedcalendar.member.exception.MemberNotFound;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.soyeon.sharedcalendar.calendar.domain.VisibilityType.*;
import static com.soyeon.sharedcalendar.common.security.SecurityUtils.getCurrentMemberId;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CalendarEventService {
    private final ValidatorService validatorService;
    private final ImgService imgService;
    private final MemberRepository memberRepository;
    private final CalendarEventRepository calendarEventRepository;
    private final CalendarCategoryRepository calendarCategoryRepository;

    private final String defaultEventColor = "#42a5f5";

    /**
     * 기간 내 일정을 모두 조회한다.
     * @param calendarId
     * @param from
     * @param to
     * @return
     */
    public List<CalendarEvent> getEvents(Long calendarId, LocalDateTime from, LocalDateTime to) {
        validatorService.validateCalendar(calendarId);
        return calendarEventRepository.findReadable(calendarId, getCurrentMemberId(), from, to);
    }

    /**
     * 새로운 일정을 등록한다.
     * @param calendarId
     * @param request
     */
    @Transactional
    public void createEvent(Long calendarId, CalendarEventRequest request) {
        // 1. 유효 체크
        validatorService.validateCalendar(calendarId);

        CalendarEvent event = CalendarEvent.create(calendarId, getCurrentMemberId(), defaultEventColor, request);
        // 2. 카테고리 있으면 카테고리 색상 적용 없으면 기본 색상 적용
        if (request.categoryId() != null) {
            String color = getEventColorByCategory(request.categoryId());
            event.changeColor(color);
        }

        // 3. 공개 범위
        switch (request.visibility()) {
            case PUBLIC:
                event.changeVisibilityToPublic();
                event.allowAll();
                break;
            case PRIVATE:
                Set<Member> visibleMembers = request.visibleMemberIds()
                        .stream()
                        .map((String id) -> memberRepository.getReferenceById(Long.valueOf(id)))
                        .collect(Collectors.toSet());
                event.allowOnly(visibleMembers);
                break;
        }
        calendarEventRepository.save(event);
    }

    /**
     * 일정을 삭제한다. (일정을 생성한 사람이 삭제할 수 있음)
     * @param calendarId
     * @param eventId
     */
    @Transactional
    public void deleteEvent(Long calendarId, Long eventId) {
        validatorService.validateCalendar(calendarId);

        CalendarEvent event = calendarEventRepository
                .getCalendarEventByCalendarEventIdAndCalendarId(eventId, calendarId)
                .orElseThrow(() -> new EventNotFound(calendarId, eventId));
        if (!event.getCreatedBy().equals(getCurrentMemberId())) {
            throw new EventUnauthorized(calendarId, eventId,"해당 일정을 생성한 사용자만 삭제할 수 있습니다.");
        }
        calendarEventRepository.delete(event);
    }

    /**
     * 일정을 수정한다. (일정을 생성한 사람이 수정할 수 있음)
     * @param calendarId
     * @param eventId
     * @param request
     */
    @Transactional
    public void updateEvent(Long calendarId, Long eventId, CalendarEventRequest request) {
        Long memberId = getCurrentMemberId();
        CalendarEvent event = calendarEventRepository
                .getCalendarEventByCalendarEventIdAndCalendarId(eventId, calendarId)
                .orElseThrow(() -> new EventNotFound(calendarId, eventId));
        if (event.getVisibility() == PUBLIC
            && !event.getCreatedBy().equals(memberId)) {
            throw new EventUnauthorized(calendarId, eventId);
        }

        // 카테고리 변경에 따라 일정 색상(color) 수정
        String color = event.getColor();

        // case 1: 카테고리 정보를 삭제하는 경우
        if (request.categoryId() == null) {
            color = defaultEventColor;
        }

        // case 2: 카테고리를 새로 등록하는 경우
        if (request.categoryId() != null) {
            Long originCategory = event.getCategoryId();
            Long newCategory = request.categoryId();
            // case 2-1: 원래 카테고리가 없는데 카테고리를 새로 등록하는 경우
            if (originCategory == null)  {
                color = getEventColorByCategory(request.categoryId());
            }
            // case 2-2: 원래 카테고리를 다른 카테고리로 수정하는 경우
            if ( originCategory != null
                    && !(originCategory.equals(newCategory))) {
                color = getEventColorByCategory(request.categoryId());
            }
        }

        CalendarEvent updated = CalendarEvent.create(calendarId, memberId, color, request);

        // 공개 범위 변경 시 수정
        // case 1: PRIVATE -> PUBLIC
        boolean toPublic = request.visibility() == PUBLIC;
        if (event.getVisibility() == PRIVATE && toPublic) {
            updated.changeVisibilityToPublic();
            updated.allowAll();
        }
        // case 2: PUBLIC -> PRIVATE
        // case 3: PRIVATE -> PRIVATE (멤버 추가 또는 삭제)
        if (!toPublic) {
            Set<Member> visibleMembers = request.visibleMemberIds()
                    .stream()
                    .map((String id) -> memberRepository.getReferenceById(Long.valueOf(id)))
                    .collect(Collectors.toSet());
            updated.allowOnly(visibleMembers);
        }
        calendarEventRepository.update(memberId, updated);
    }

    /**
     * 카테고리가 있으면 카테고리 색상을, 없으면 기본 색상을 반환한다.
     * @param categoryId
     * @return
     */
    private String getEventColorByCategory(Long categoryId) {
        CalendarCategory category = calendarCategoryRepository
                .findById(categoryId)
                .orElse(null);
        return category == null ? defaultEventColor : category.getCategoryColor();
    }

    /**
     * 일정을 조회한다. (단건)
     * @param calendarId
     * @param eventId
     * @return
     */
    public CalendarEventDetailResponse getEvent(Long calendarId, Long eventId) {
        Long memberId = getCurrentMemberId();
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new MemberNotFound(memberId));
        CalendarEvent event = validatorService.validateEvent(calendarId, eventId);
        boolean isCreatedByMe = event.getCreatedBy().equals(memberId);

        List<CalendarCategoriesResponse> categories = calendarCategoryRepository
                .findByCalendarId(calendarId)
                .stream()
                .map(CalendarCategoriesResponse::from)
                .toList();

        Set<MemberDto> visibleMembers = event.getVisibilityMembers()
                .stream()
                .map(ev -> {
                    String presignedUrl = imgService.getPresignedUrlByObjectKey(ev.getMember().getProfileImgKey());
                    return MemberDto.create(ev.getMember().getName(), ev.getMember().getEmail(), presignedUrl);
                })
                .collect(Collectors.toSet());
        return CalendarEventDetailResponse.from(event, categories, visibleMembers, member.getName(), isCreatedByMe);
    }
}
