package com.soyeon.sharedcalendar.auth.dto.userinfo;

import com.soyeon.sharedcalendar.auth.config.AuthProperties;
import com.soyeon.sharedcalendar.auth.domain.SocialProvider;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;

import static com.soyeon.sharedcalendar.auth.domain.SocialProvider.*;

@Component
@RequiredArgsConstructor
public class UserInfoRegistry {
    private final AuthProperties props;

    public record Meta(String userInfoUri, Class<? extends OAuthUserInfo> provider) {}

    private final Map<SocialProvider, Meta> map = new EnumMap<>(SocialProvider.class);

    @PostConstruct
    public void init() {
        map.put(GOOGLE, new Meta(
            props.getProviders().get(GOOGLE).getUserInfoUri(),
            GoogleUserInfo.class
        ));
    }

    public Meta getMeta(SocialProvider provider) {
        return map.get(provider);
    }
}
