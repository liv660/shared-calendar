package com.soyeon.sharedcalendar.auth.pkce.service;

import com.soyeon.sharedcalendar.auth.config.AuthProperties;
import com.soyeon.sharedcalendar.auth.domain.OAuth2Provider;
import com.soyeon.sharedcalendar.auth.pkce.domain.PKCELoginContext;
import com.soyeon.sharedcalendar.auth.pkce.dto.PKCEInitResponse;
import com.soyeon.sharedcalendar.auth.pkce.PKCEUtils;
import com.soyeon.sharedcalendar.auth.pkce.repository.PKCEStateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class PKCEService {
    private final PKCEStateRepository pkceRepository;
    private final String userAgent = "shared-calendar-api/1.0";
    private final AuthProperties props;

    private static final Duration STATE_TTL = Duration.ofMinutes(5);

    @Value("${spring.data.redis.host}")
    private String ip;

    @Value("${auth.providers.kakao.issuer}")
    private String authorizeBaseUrl;

    /**
     * 앱에서 카카오 인가 코드 발급을 위한 verifier를 생성한다.
     * @return
     */
    public PKCEInitResponse createVerifier() {
        AuthProperties.Provider kakao = props.getProviders().get(OAuth2Provider.KAKAO);
        String state = PKCEUtils.generateState();
        String codeVerifier = PKCEUtils.generateCodeVerifier();
        String codeChallenge = PKCEUtils.toCodeChallenge(codeVerifier);
        String redirectUri = URLEncoder.encode(kakao.getRedirectUri(), StandardCharsets.UTF_8);
        System.out.println("redirectUri = " + redirectUri);
        pkceRepository.save(state,
        new PKCELoginContext(codeVerifier,
                redirectUri,
                System.currentTimeMillis(),
                userAgent,
                ip),
        STATE_TTL);

        String authorizeUrl = UriComponentsBuilder
                .fromUri(URI.create(authorizeBaseUrl + "/oauth/authorize"))
                .queryParam("client_id=" + kakao.getClientId())
                .queryParam("redirect_uri=" + redirectUri)
                .queryParam("response_type", "code")
                .queryParam("scope=" + "openid profile_nickname profile_image account_email")
                .queryParam("state=" + state)
                .queryParam("code_challenge=" + codeChallenge)
                .queryParam("code_challenge_method=" + "S256")
                .build().toUriString();
        PKCEInitResponse initResponse = new PKCEInitResponse(authorizeUrl, state, STATE_TTL.toSeconds());
        return initResponse;
    }
}
