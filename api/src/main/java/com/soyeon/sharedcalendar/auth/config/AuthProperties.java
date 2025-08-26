package com.soyeon.sharedcalendar.auth.config;

import com.soyeon.sharedcalendar.auth.domain.OAuth2Provider;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.EnumMap;
import java.util.Map;

@Getter @Setter
@ConfigurationProperties(prefix = "auth")
public class AuthProperties {
    private Map<OAuth2Provider, Provider> providers =  new EnumMap<>(OAuth2Provider.class);

    @Getter @Setter
    public static class Provider {
        private String clientId;
        private String clientSecret;
        private String tokenBaseUri;
        private String issuer;
        private String jwksUri;
        private String userInfoUri;
        private String redirectUri;
    }
}
