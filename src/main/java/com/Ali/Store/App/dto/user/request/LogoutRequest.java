package com.Ali.Store.App.dto.user.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class LogoutRequest {

    @NotNull(message = "User id can't be null !")
    @Positive(message = "User id must be a positive value !")
    private Long userId;

    @NotBlank(message = "Device UUID can't be null !")
    private UUID deviceUuid;
}
