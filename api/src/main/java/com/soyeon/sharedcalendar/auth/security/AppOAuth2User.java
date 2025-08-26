package com.soyeon.sharedcalendar.auth.security;

import com.soyeon.sharedcalendar.auth.domain.OAuth2Provider;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.soyeon.sharedcalendar.auth.domain.OAuth2Provider.*;

@Getter
public class AppOAuth2User implements OAuth2User, OidcUser {
    private Map<String, Object> attributes;
    OAuth2Provider provider;
    private String providerUserId;
    private String name;
    private String email;
    private String profileImgUrl;

    private AppOAuth2User(Map<String, Object> attributes,
                          OAuth2Provider provider,
                          String providerUserId,
                          String name,
                          String email,
                          String profileImgUrl) {
        this.attributes = attributes;
        this.provider = provider;
        this.providerUserId = providerUserId;
        this.name = name;
        this.email = email;
        this.profileImgUrl = profileImgUrl;
    }

    public static OAuth2User fromGoogle(OidcUser user) {
        Map<String, Object> attr = user.getAttributes();
        System.out.println("attr = " + attr);
        String providerUserId = attr.get("sub").toString();
        String name = attr.get("given_name").toString();
        String email = attr.get("email").toString();
        String profileImgUrl = attr.get("picture").toString();
        return new AppOAuth2User(attr, GOOGLE, providerUserId, name, email, profileImgUrl);
    }

    public static OAuth2User fromKakao(OAuth2User user) {
        Map<String, Object> attr = user.getAttributes();
        String providerUserId = attr.get("sub").toString();
        String name = attr.get("nickname").toString();
        String email = attr.get("email").toString();
        String profileImgUrl = attr.get("picture").toString();
        return new AppOAuth2User(attr, KAKAO, providerUserId, name, email, profileImgUrl);
    }

    //OAuth
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    //OAuth
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    //OAuth
    @Override
    public String getName() {
        return provider.name();
    }

    //Oidc
    @Override
    public Map<String, Object> getClaims() {
        return attributes;
    }

    //Oidc
    @Override
    public OidcUserInfo getUserInfo() {
        return new OidcUserInfo(attributes);
    }

    //Oidc
    @Override
    public OidcIdToken getIdToken() {
        return new OidcIdToken((String) attributes.get("at_hash"), (Instant) attributes.get("iat"), (Instant) attributes.get("exp"), attributes);
    }
}
