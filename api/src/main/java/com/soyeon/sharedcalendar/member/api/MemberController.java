package com.soyeon.sharedcalendar.member.api;

import com.soyeon.sharedcalendar.member.app.MemberService;
import com.soyeon.sharedcalendar.member.dto.MeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
@Tag(name = "Member", description = "회원 정보 관련 API")
public class MemberController {
    private final MemberService memberService;

    @Operation(summary = "내 정보 조회", description = "기본 정보를 조회한다.")
    @GetMapping("/me")
    public MeResponse me() {
        return memberService.getCurrentMemberSummary();
    }
}
