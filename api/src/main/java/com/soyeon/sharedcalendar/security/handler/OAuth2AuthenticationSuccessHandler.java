package com.soyeon.sharedcalendar.security.handler;

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
    private final ApplicationEventPublisher eventPublisher;

    @Value("${app.front.redirect-uri}")
    private String frontRedirectUri;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        AppOAuth2User user = (AppOAuth2User) authentication.getPrincipal();
        Member member = memberService.findOrCreate(user);
        eventPublisher.publishEvent(new OAuthLoginSuccessEvent(member.getMemberId(), user.getProfileImgUrl()));

        TokenResponse tokens = tokenService.issueToken(member);
        String hashedRefreshToken = tokenService.getHashedRefreshToken(tokens.refreshToken());
        memberService.updateRefreshToken(member.getMemberId(), hashedRefreshToken);

        MemberAuthenticationToken authed = MemberAuthenticationToken.auth(new MemberPrincipal(member.getMemberId(),
                        member.getEmail(),
                        member.getName())
                , tokens);
        SecurityContextHolder.getContext().setAuthentication(authed);

        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        response.setContentType("application/json;charset=UTF-8");
        response.addHeader("Set-Cookie", buildJwtCookie("access_token", tokens.accessToken(), Duration.ofHours(6)).toString());
        response.addHeader("Set-Cookie", buildJwtCookie("refresh_token", tokens.refreshToken(), Duration.ofDays(7)).toString());
        getRedirectStrategy().sendRedirect(request, response, frontRedirectUri);
    }

    private ResponseCookie buildJwtCookie(String name, String token, Duration maxAge) {
        return ResponseCookie.from(name, token)
                .httpOnly(true).path("/")
                .sameSite("Lax").secure(false)
                .maxAge(maxAge).build();
    }
}
