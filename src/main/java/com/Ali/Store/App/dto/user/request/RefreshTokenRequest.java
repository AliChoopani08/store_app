package com.Ali.Store.App.dto.user.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class RefreshTokenRequest {
    @NotNull(message = "Refresh Token must not be empty !")
    @Schema(description = "The token saved in database of refresh token")
    @JsonProperty("Refresh Token")
    private UUID token;
}
