package com.soyeon.sharedcalendar.auth.security;

import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

@Component
public class CustomOAuthUserService implements OAuth2UserService {
    private final OidcUserService oidc = new OidcUserService();
    private final DefaultOAuth2UserService oauth = new DefaultOAuth2UserService();


    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {
        String provider = request.getClientRegistration().getRegistrationId();

        if (provider.equalsIgnoreCase("google")) {
            OidcUser user = oidc.loadUser((OidcUserRequest) request);
            return AppOAuth2User.fromGoogle(user);
        } else if (provider.equalsIgnoreCase("kakao")) {
            OAuth2User user = oauth.loadUser(request);
            return AppOAuth2User.fromKakao(user);
        }
        throw new OAuth2AuthenticationException("unsupported provider: " + provider);
    }
}
