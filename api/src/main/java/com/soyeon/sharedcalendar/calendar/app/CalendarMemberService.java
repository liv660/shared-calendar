package com.soyeon.sharedcalendar.calendar.app;

import com.soyeon.sharedcalendar.calendar.domain.CalendarAccessLevel;
import com.soyeon.sharedcalendar.calendar.domain.CalendarMember;
import com.soyeon.sharedcalendar.calendar.domain.MemberRole;
import com.soyeon.sharedcalendar.calendar.domain.repository.CalendarMemberRepository;
import com.soyeon.sharedcalendar.calendar.dto.response.CalendarMemberResponse;
import com.soyeon.sharedcalendar.common.img.app.ImgService;
import com.soyeon.sharedcalendar.common.security.SecurityUtils;
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
    private final CalendarMemberRepository calendarMemberRepository;
    private final MemberRepository memberRepository;
    private final ImgService imgService;

    /**
     * 캘린더의 사용자로 등록한다.
     * @param calendarId
     * @param role ADMIN / USER
     * @param accessLevel READ_ONLY, READ_WRITE, FULL_ACCESS
     */
    @Transactional
    public void addMember(Long calendarId, MemberRole role, CalendarAccessLevel accessLevel) {
        Long memberId = SecurityUtils.getCurrentMemberId();
        CalendarMember member = CalendarMember.create(calendarId, memberId, role, accessLevel);
        calendarMemberRepository.save(member);
    }

    /**
     * 캘린더 내 특정 사용자의 권한을 조회한다.
     * @param calendarId
     * @param memberId
     * @return
     */
    public CalendarAccessLevel getAccessLevel(Long calendarId, Long memberId) {
        CalendarMember cm = calendarMemberRepository.findCalendarMemberByCalendarIdAndMemberId(calendarId, memberId);
        return cm.getAccessLevel();
    }

    /**
     * 캘린더 내 사용자를 조회한다.
     * @param calendarId
     * @return
     */
    public List<CalendarMemberResponse> getMembers(Long calendarId) {
        List<Long> memberIds = calendarMemberRepository.findMemberIdsByCalendarId(calendarId)
                .stream()
                .map(CalendarMember::getMemberId)
                .toList();
        List<Member> members = memberRepository.findAllById(memberIds);
        List<CalendarMemberResponse> profiles = new ArrayList<>();

        for (Member m : members) {
            String presignedUrl = imgService.getPresignedUrlByObjectKey(m.getProfileImgKey());
            profiles.add(CalendarMemberResponse.create(m.getName(), m.getEmail(), presignedUrl));
        }
        return profiles;
    }
}
