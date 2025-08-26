package com.soyeon.sharedcalendar.member.app;

import com.soyeon.sharedcalendar.auth.config.AuthProperties;
import com.soyeon.sharedcalendar.auth.config.AuthProperties.Provider;
import com.soyeon.sharedcalendar.auth.domain.OAuth2Provider;
import com.soyeon.sharedcalendar.auth.dto.response.OAuth2TokenResponse;
import com.soyeon.sharedcalendar.auth.dto.userinfo.OAuth2UserInfoMeta;
import com.soyeon.sharedcalendar.auth.dto.userinfo.OAuthUserInfo;
import com.soyeon.sharedcalendar.member.domain.Member;
import com.soyeon.sharedcalendar.member.domain.repository.MemberRepository;
import com.soyeon.sharedcalendar.member.dto.SignupRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final AuthProperties props;
    private final OAuth2UserInfoMeta OAuth2UserInfoMeta;

    @Value("${profile.default-member}")
    private String defaultProfileImgUrl;

    /**
     * 회원 가입
     * @param request
     * @return
     */
    @Transactional
    public Member signup(SignupRequest request) {
        String profileImgUrl = request.profileImgUrl() == null ? defaultProfileImgUrl : request.profileImgUrl();
        Member member = Member.create(request, profileImgUrl);
        memberRepository.save(member);
        return member;
    }

    /**
     * 재발급된 refreshToken 저장
     * @param memberId
     * @param refreshToken
     */
    @Transactional
    public void updateRefreshToken(Long memberId, String refreshToken) {
        memberRepository.updateRefreshToken(memberId, refreshToken);
    }

    /**
     * 신규 사용자이면 저장
     * @param provider
     * @param tokens OAuth에서 발급 받은 토큰 (access_token, refresh_token ...)
     * @param jwt OAuth jwt 토큰
     * @return
     */
    public Member findOrCreateMember(OAuth2Provider provider, OAuth2TokenResponse tokens, Jwt jwt) {
        return memberRepository
                .findByProviderAndProviderUserId(provider, jwt.getSubject())
                .orElseGet(() -> registerMember(provider, tokens.accessToken()));
    }

    /**
     * OAuth accessToken으로 사용자 정보 조회하여 저장
     * @param provider
     * @param accessToken
     * @return
     */
    private Member registerMember(OAuth2Provider provider, String accessToken) {
        Provider pv = props.getProviders().get(provider);
        OAuth2UserInfoMeta.UserInfo info = OAuth2UserInfoMeta.getUserInfo(provider);
        OAuthUserInfo userInfo = WebClient.builder()
                .baseUrl(info.userInfoUri())
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .build()
                .get().retrieve().bodyToMono(info.provider()).block();
        if (userInfo == null) {
            throw new InternalAuthenticationServiceException("failed to retrieve user info");
        }
        SignupRequest request = new SignupRequest(provider, userInfo.getUserId(), userInfo.getEmail(), userInfo.getName(), userInfo.getProfileImgUrl());
        return signup(request);
    }
}
