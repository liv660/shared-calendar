package com.soyeon.sharedcalendar.calendar.app;

import com.soyeon.sharedcalendar.calendar.domain.Calendar;
import com.soyeon.sharedcalendar.calendar.domain.CalendarAccessLevel;
import com.soyeon.sharedcalendar.calendar.domain.CalendarMember;
import com.soyeon.sharedcalendar.calendar.domain.MemberRole;
import com.soyeon.sharedcalendar.calendar.domain.repository.CalendarMemberRepository;
import com.soyeon.sharedcalendar.calendar.dto.response.CalendarMemberResponse;
import com.soyeon.sharedcalendar.common.img.app.ImgService;
import com.soyeon.sharedcalendar.common.security.SecurityUtils;
import com.soyeon.sharedcalendar.common.validator.ValidatorService;
import com.soyeon.sharedcalendar.member.domain.Member;
import com.soyeon.sharedcalendar.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CalendarMemberService {
    private final ValidatorService validatorService;
    private final CalendarMemberRepository calendarMemberRepository;
    private final MemberRepository memberRepository;
    private final ImgService imgService;


    /**
     * 캘린더로 초대받은 사용자를 등록한다.
     * @param calendarId
     * @param member
     * @param accessLevel
     */
    public void addMember(Long calendarId, Member member, CalendarAccessLevel accessLevel) {
        Calendar c = validatorService.validateCalendar(calendarId);
        CalendarMember cm = CalendarMember.create(calendarId, member, MemberRole.USER, accessLevel);
        calendarMemberRepository.save(cm);

    }

    /**
     * 캘린더 생성자를 캘린더의 사용자로 등록한다.
     * @param calendarId
     */
    @Transactional
    public void initMember(Long calendarId) {
        Long memberId = SecurityUtils.getCurrentMemberId();
        Member member = validatorService.validateMember(memberId);
        CalendarMember cm = CalendarMember.create(
                calendarId,
                member,
                MemberRole.ADMIN,
                CalendarAccessLevel.FULL_ACCESS);
        calendarMemberRepository.save(cm);
    }

    /**
     * 캘린더 내 특정 사용자의 권한을 조회한다.
     * @param calendarId
     * @param memberId
     * @return
     */
    public CalendarAccessLevel getAccessLevel(Long calendarId, Long memberId) {
        validatorService.validateCalendar(calendarId);
        Member member = validatorService.validateMember(memberId);
        CalendarMember cm = calendarMemberRepository.findCalendarMemberByCalendarIdAndMember(calendarId, member);
        return cm.getAccessLevel();
    }

    /**
     * 캘린더 내 사용자를 조회한다.
     * @param calendarId
     * @return
     */
    public List<CalendarMemberResponse> getMembers(Long calendarId) {
        Calendar calendar = validatorService.validateCalendar(calendarId);
        List<CalendarMember> list = calendarMemberRepository.findAllMemberByCalendarId(calendarId);

        List<CalendarMemberResponse> members = new ArrayList<>();
        list.forEach(cm -> {
            String presignedUrl = imgService.getPresignedUrlByObjectKey(cm.getMember().getProfileImgKey());
            members.add(CalendarMemberResponse.create(cm.getMember().getMemberId(),
                    cm.getMember().getName(),
                    cm.getMember().getEmail(),
                    presignedUrl,
                    cm.getAccessLevel(),
                    calendar.getOwnerId().equals(cm.getMember().getMemberId())));

        });
        return members;
    }

    /**
     * 사용자의 권한을 변경한다.
     * @param calendarId
     * @param memberId
     * @param accessLevel 새로 적용할 권한
     */
    @Transactional
    public void changeAccessLevel(Long calendarId, Long memberId, CalendarAccessLevel accessLevel) {
        Calendar calendar = validatorService.validateCalendar(calendarId);
        Long contextMemberId = SecurityUtils.getCurrentMemberId();

        validatorService.isOwner(calendar, contextMemberId);
        Member member = validatorService.validateMember(memberId);

        CalendarMember cm = calendarMemberRepository.findCalendarMemberByCalendarIdAndMember(calendarId, member);
        cm.changeAccessLevel(accessLevel);
        calendarMemberRepository.save(cm);
    }

    /**
     * 캘린더 내에서 사용자를 삭제한다.
     * @param calendarId
     * @param memberId
     */
    @Transactional
    public void deleteMember(Long calendarId, Long memberId) {
        Calendar calendar = validatorService.validateCalendar(calendarId);
        Member member = validatorService.validateMember(memberId);
        Long contextMemberId = SecurityUtils.getCurrentMemberId();
        validatorService.isOwner(calendar, contextMemberId);
        calendarMemberRepository.deleteByCalendarIdAndMember(calendarId, member);
    }
}
