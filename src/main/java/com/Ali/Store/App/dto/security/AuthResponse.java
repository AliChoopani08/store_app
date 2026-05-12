package com.Ali.Store.App.dto.security;

import com.Ali.Store.App.dto.user.response.DeviceSummary;
import com.Ali.Store.App.dto.user.response.UserSummary;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@Getter
@ToString(of = {"accessToken"})
@EqualsAndHashCode(of = {"accessToken"})
@Builder
public class AuthResponse {

    @JsonProperty("Refresh Token")
    private UUID refreshToken;
    @JsonProperty("Access Token")
    private String accessToken;
    @JsonProperty("User")
    private UserSummary userResponse;
    @JsonProperty("device UUID")
    private UUID deviceUuid;


}
