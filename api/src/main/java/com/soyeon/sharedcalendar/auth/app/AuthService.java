package com.soyeon.sharedcalendar.auth.app;

import com.soyeon.sharedcalendar.auth.config.AuthProperties;
import com.soyeon.sharedcalendar.auth.config.AuthProperties.Provider;
import com.soyeon.sharedcalendar.auth.domain.OAuth2Provider;
import com.soyeon.sharedcalendar.auth.dto.response.OAuth2TokenResponse;
import com.soyeon.sharedcalendar.auth.pkce.domain.PKCELoginContext;
import com.soyeon.sharedcalendar.auth.pkce.repository.PKCEStateRepository;
import com.soyeon.sharedcalendar.auth.security.OAuth2IdTokenDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthProperties props;
    private final OAuth2IdTokenDecoder decoder;
    private final PKCEStateRepository pkceStateRepository;

    /**
     * 인가 코드로 idToken을 발급 받는다.
     * @param provider
     * @param code
     * @param state
     * @return
     */
    public OAuth2TokenResponse exchangeCodeForTokens(OAuth2Provider provider, String code, String state) {
        return switch (provider) {
            case GOOGLE -> getGoogleTokens(provider, code);
            case KAKAO -> getKakaoTokens(provider, code, state);
        };
    }

    /**
     * idToken을 검증한다.
     * @param tokens
     * @return
     */
    public Jwt validateIdToken(OAuth2Provider provider, OAuth2TokenResponse tokens) {
        Jwt jwt = decoder.get(provider).decode(tokens.idToken());
        boolean validated = validateJwt(provider, jwt);
        return validated ? jwt : null;
    }

    /**
     * google idToken을 발급한다.
     * @param provider
     * @param code
     * @return
     */
    private OAuth2TokenResponse getGoogleTokens(OAuth2Provider provider, String code) {
        Provider google = props.getProviders().get(provider);
        WebClient client = WebClient.builder().baseUrl(google.getTokenBaseUri()).build();
        OAuth2TokenResponse tokens = client.post()
                .uri("/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("code", code)
                        .with("client_id", google.getClientId())
                        .with("client_secret", google.getClientSecret())
                        .with("grant_type", "authorization_code")
                )
                .retrieve()
                .onStatus(HttpStatusCode::isError, r -> r.bodyToMono(String.class)
                        .map(body -> new IllegalStateException("Google token error: " + body)))
                .bodyToMono(OAuth2TokenResponse.class)
                .block();
        return tokens;
    }


    /**
     * 카카오 idToken 발급한다.
     * @param provider
     * @param code
     * @param state
     * @return
     */
    private OAuth2TokenResponse getKakaoTokens(OAuth2Provider provider, String code, String state) {
        Provider kakao = props.getProviders().get(provider);
        PKCELoginContext context = pkceStateRepository.find(state);
        if (context == null) {
            throw new IllegalStateException("Kakao callback context is null.");
        }

        WebClient client = WebClient.builder().baseUrl(kakao.getTokenBaseUri()).build();
        OAuth2TokenResponse tokens = client.post()
                .uri("/oauth/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("code", code)
                        .with("client_id", kakao.getClientId())
                        .with("client_secret", kakao.getClientSecret())
                        .with("redirect_uri", kakao.getRedirectUri())
                        .with("code_verifier", context.codeVerifier())
                        .with("grant_type", "authorization_code")
                )
                .retrieve()
                .onStatus(HttpStatusCode::isError, r -> r.bodyToMono(String.class)
                        .map(body -> new IllegalStateException("Kakao token error: " + body)))
                .bodyToMono(OAuth2TokenResponse.class)
                .block();
        return tokens;
    }

    /**
     * idToken을 통해 응답 받은 jwt가 유효한지 검증한다.
     * @param provider
     * @param jwt
     * @return
     */
    private boolean validateJwt(OAuth2Provider provider, Jwt jwt) {
        Provider pv = props.getProviders().get(provider);
        // 만료
        Instant exp = jwt.getExpiresAt();
        if (exp == null || exp.isBefore(Instant.now())) {
            throw new BadCredentialsException("Expired JWT token");
        }

        // issuer
        String issuer = jwt.getIssuer() != null ? jwt.getIssuer().toString() : null;
        String expectedIss = pv.getIssuer();
        if (!expectedIss.equals(issuer)) {
            throw new BadCredentialsException("Invalid JWT token (issuer)");
        }

        // audience
        List<String> aud = jwt.getAudience();
        String clientId = pv.getClientId();
        if (aud == null || !aud.contains(clientId)) {
            throw new BadCredentialsException("Invalid JWT token (audience)");
        }
        return true;
    }
}
