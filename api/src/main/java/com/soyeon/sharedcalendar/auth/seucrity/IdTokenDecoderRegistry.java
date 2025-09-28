package com.soyeon.sharedcalendar.auth.seucrity;

import com.soyeon.sharedcalendar.auth.config.AuthProperties;
import com.soyeon.sharedcalendar.auth.domain.ProviderType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

import static com.soyeon.sharedcalendar.auth.config.AuthProperties.*;

@Component
@RequiredArgsConstructor
public class IdTokenRegistry {
    private final AuthProperties properties;
    private final Map<ProviderType, JwtDecoder> cache = new EnumMap<>(ProviderType.class);

    public JwtDecoder get(ProviderType provider) {
        return cache.computeIfAbsent(provider, this::buildDecoder);
    }

    private JwtDecoder buildDecoder(ProviderType provider) {
        Provider pv = properties.getProviders().get(provider);
        if (pv == null) {
            throw new IllegalArgumentException("Unknown provider: " + provider);
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
