package com.soyeon.sharedcalendar.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OAuth2TokenResponse(@JsonProperty("token_type") String tokenType,
                                  @JsonProperty("id_token") String idToken,
                                  @JsonProperty("access_token") String accessToken,
                                  @JsonProperty("refresh_token") String refreshToken,
                                  @JsonProperty("expires_in") String expiresIn,
                                  @JsonProperty("scope") String scope) {
}
