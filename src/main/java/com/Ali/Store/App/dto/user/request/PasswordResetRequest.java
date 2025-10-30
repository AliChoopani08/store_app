package com.Ali.Store.App.dto.user.request;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class PasswordResetRequest {
        @NotBlank(message = "Password Rest Token can't be empty")
        @JsonProperty("Password Rest Token")
        private String passwordRestToken;

        @NotBlank(message = "New password can't be empty !")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d@$%._*!?]{8,}$",
                message = "Password must be combination of (capital letters, lowercase letters, number , special characters) !")
        @JsonProperty("New Password")
        public String newPassword;
}
