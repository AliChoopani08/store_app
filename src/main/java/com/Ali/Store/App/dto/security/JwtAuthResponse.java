package com.Ali.Store.App.dto.security;

import com.Ali.Store.App.dto.user.response.UserSummary;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@AllArgsConstructor
@Getter
@ToString(of = {"accessToken"})
@EqualsAndHashCode(of = {"accessToken"})
@Builder
public class JwtAuthResponse {

    @JsonProperty("Refresh Token")
    private String refreshToken;
    @JsonProperty("Access Token")
    private String accessToken;
    @JsonProperty("User")
    private UserSummary userResponse;

}
