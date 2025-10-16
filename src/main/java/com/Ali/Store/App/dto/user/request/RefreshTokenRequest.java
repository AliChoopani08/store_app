package com.Ali.Store.App.dto.user.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class RefreshTokenRequest {
    @NotBlank(message = "Refresh Token must not be empty !")
    @Schema(description = "The token saved in database of refresh token")
    @JsonProperty("Refresh Token")
    private String refreshToken;

    @NotBlank(message = "Device Id is empty !")
    @Size(min = 10, max = 500, message = "Device Id must be between 1 until 500 character !")
    @Schema(description = "Unique identification for each device of user -> (It must be made randomly by Front End)")
    private String deviceId;
}
