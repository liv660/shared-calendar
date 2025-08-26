package com.soyeon.sharedcalendar.auth.dto.userinfo;

import com.soyeon.sharedcalendar.auth.config.AuthProperties;
import com.soyeon.sharedcalendar.auth.domain.OAuth2Provider;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;

import static com.soyeon.sharedcalendar.auth.domain.OAuth2Provider.*;

@Component
@RequiredArgsConstructor
public class OAuth2UserInfoMeta {
    private final AuthProperties props;

    public record UserInfo(String userInfoUri, Class<? extends OAuthUserInfo> provider) {}

    private final Map<OAuth2Provider, UserInfo> map = new EnumMap<>(OAuth2Provider.class);

    @PostConstruct
    public void init() {
        map.put(GOOGLE, new UserInfo(
            props.getProviders().get(GOOGLE).getUserInfoUri(),
            GoogleUserInfo.class
        ));
        map.put(KAKAO, new UserInfo(
                props.getProviders().get(KAKAO).getUserInfoUri(),
                KakaoUserInfo.class
        ));
    }

    public UserInfo getUserInfo(OAuth2Provider provider) {
        return map.get(provider);
    }
}
