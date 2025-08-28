package com.soyeon.sharedcalendar.security.oauth2;

import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;

@Component
public class CustomOidcUserService implements OAuth2UserService<OidcUserRequest, OidcUser> {
    private final OidcUserService oidc = new OidcUserService();

    @Override
    public OidcUser loadUser(OidcUserRequest request) throws OAuth2AuthenticationException {
        String provider = request.getClientRegistration().getRegistrationId();

        if (provider.equalsIgnoreCase("google")) {
            OidcUser user = oidc.loadUser(request);
            return AppOAuth2User.fromGoogle(user);
        }
        throw new OAuth2AuthenticationException("unsupported oidc provider: " + provider);
    }
}
