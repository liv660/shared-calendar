package com.soyeon.sharedcalendar.security.handler;

import com.soyeon.sharedcalendar.common.crypto.HashingService;
import com.soyeon.sharedcalendar.invite.app.InviteService;
import com.soyeon.sharedcalendar.member.domain.OAuthLoginSuccessEvent;
import com.soyeon.sharedcalendar.security.core.MemberAuthenticationToken;
import com.soyeon.sharedcalendar.security.core.MemberPrincipal;
import com.soyeon.sharedcalendar.token.app.TokenService;
import com.soyeon.sharedcalendar.token.dto.response.TokenResponse;
import com.soyeon.sharedcalendar.security.oauth2.AppOAuth2User;
import com.soyeon.sharedcalendar.member.app.MemberService;
import com.soyeon.sharedcalendar.member.domain.Member;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final TokenService tokenService;
    private final MemberService memberService;
    private final InviteService inviteService;
    private final ApplicationEventPublisher eventPublisher;

    @Value("${app.front.base-url}")
    private String frontBaseUrl;

    @Value("${app.front.login-redirect-url}")
    private String frontRedirectUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        AppOAuth2User user = (AppOAuth2User) authentication.getPrincipal();
        Member member = memberService.findMember(user.getProvider(), user.getProviderUserId());
        String redirectUrl = frontRedirectUrl;

        //신규 회원 -> 프로필 이미지 update event
        if (member == null) {
            member = memberService.createMember(user);
            eventPublisher.publishEvent(new OAuthLoginSuccessEvent(member, user.getProfileImgUrl()));

            // 초대 받은 이력 있는 경우 초대 상태 업데이트
            boolean exists = inviteService.existsInviteFor(member.getEmail());
            if (exists) {
                Long calendarId = inviteService.markInviteAsJoined(member);
                redirectUrl = (frontBaseUrl + "/calendars/" + calendarId);
            }
        }

        TokenResponse tokens = tokenService.issueToken(member);
        String hash = HashingService.hash(tokens.refreshToken());
        memberService.updateRefreshToken(member, hash);

        MemberAuthenticationToken authed = MemberAuthenticationToken.auth(new MemberPrincipal(member.getMemberId(),
                        member.getEmail(),
                        member.getName())
                , tokens);
        SecurityContextHolder.getContext().setAuthentication(authed);

        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        response.setContentType("application/json;charset=UTF-8");
        response.addHeader("Set-Cookie", buildJwtCookie("access_token", tokens.accessToken(), Duration.ofHours(6)).toString());
        response.addHeader("Set-Cookie", buildJwtCookie("refresh_token", tokens.refreshToken(), Duration.ofDays(7)).toString());
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }

    private ResponseCookie buildJwtCookie(String name, String token, Duration maxAge) {
        return ResponseCookie.from(name, token)
                .httpOnly(true).path("/")
                .sameSite("Lax").secure(false)
                .maxAge(maxAge).build();
    }
}
