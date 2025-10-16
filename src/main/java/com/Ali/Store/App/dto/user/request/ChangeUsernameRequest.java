package com.Ali.Store.App.dto.user.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ChangeUsernameRequest {

    @NotBlank(message = "New username expression can't be empty !")
    @Pattern(regexp = "^(09\\d{9}|[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+)$", message = "Username must be a valid email or phone number !")
    @Schema(description = "New UserName")
    private String newUsername;

}
