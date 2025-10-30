package com.Ali.Store.App.dto.security;

import com.Ali.Store.App.dto.user.response.UserResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class PwdVerifyJwtResponse {
    @JsonProperty("Password Verify Token")
    private String passwordVerifyToken;

    @JsonProperty("User")
    private UserResponse userResponse;
}
