package com.Ali.Store.App.dto.security;

import com.Ali.Store.App.dto.user.response.UserResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString(of = {"accessToken"})
@EqualsAndHashCode(of = {"accessToken"})
public class AuthJwtResponse {

    @JsonProperty("Refresh Token")
    private String refreshToken;
    @JsonProperty("Access Token")
    private String accessToken;
    @JsonProperty("User")
    private UserResponse userResponse;

    public AuthJwtResponse(String token) {
        this.accessToken = token;
    }
}
