package com.soyeon.sharedcalendar.auth.security;

import com.soyeon.sharedcalendar.auth.config.AuthProperties;
import com.soyeon.sharedcalendar.auth.config.AuthProperties.Provider;
import com.soyeon.sharedcalendar.auth.domain.OAuth2Provider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2IdTokenDecoder {
    private final AuthProperties props;
    private final Map<OAuth2Provider, JwtDecoder> cache = new EnumMap<>(OAuth2Provider.class);

    public JwtDecoder get(OAuth2Provider provider) {
        return cache.computeIfAbsent(provider, this::buildDecoder);
    }

    private JwtDecoder buildDecoder(OAuth2Provider provider) {
        Provider pv = props.getProviders().get(provider);
        if (pv == null) {
            throw new IllegalArgumentException("Unknown provider " + provider);
        }

        NimbusJwtDecoder decoder = NimbusJwtDecoder.withJwkSetUri(pv.getJwksUri()).build();
        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(pv.getIssuer());
        OAuth2TokenValidator<Jwt> claimValidator = new JwtClaimValidator<>("aud", aud -> {
            if (aud instanceof String s) return s.equals(pv.getClientId());
            if (aud instanceof Collection<?> c) return c.contains(pv.getClientId());
            return false;
        });

        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(withIssuer, claimValidator));
        return decoder;
    }
}
