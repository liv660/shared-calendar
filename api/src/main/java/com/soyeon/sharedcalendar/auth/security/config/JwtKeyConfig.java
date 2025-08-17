package com.soyeon.sharedcalendar.auth.security.config;

import com.soyeon.sharedcalendar.auth.config.JwtProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Configuration
public class JwtKeyConfig {
    @Bean
    SecretKey hs256SecretKey(JwtProperties props) {
        return new SecretKeySpec(props.hs256Secret().getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }

    @Bean
    JwtDecoder jwtDecoder(SecretKey hs256SecretKey) {
        return NimbusJwtDecoder.withSecretKey(hs256SecretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }
}
