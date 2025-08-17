package com.soyeon.sharedcalendar.auth.config;

import com.soyeon.sharedcalendar.auth.domain.SocialProvider;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.EnumMap;
import java.util.Map;

@Getter @Setter
@ConfigurationProperties(prefix = "auth")
public class AuthProperties {
    private Map<SocialProvider, Provider> providers =  new EnumMap<>(SocialProvider.class);

    @Getter @Setter
    public static class Provider {
        private String issuer;
        private String jwksUri;
        private String clientId;
        private String userInfoUri;
    }
}
