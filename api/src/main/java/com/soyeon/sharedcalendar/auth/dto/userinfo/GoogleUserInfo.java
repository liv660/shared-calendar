package com.soyeon.sharedcalendar.auth.dto.userinfo;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GoogleUserInfo(@JsonProperty("sub") String userId,
                             String email,
                             @JsonProperty("given_name") String name,
                             @JsonProperty("picture") String profileImgUrl) implements OAuthUserInfo {

    @Override
    public String getUserId() {
        return userId;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getProfileImgUrl() {
        return profileImgUrl;
    }
}
