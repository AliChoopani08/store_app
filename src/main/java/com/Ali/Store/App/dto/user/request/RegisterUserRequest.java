package com.Ali.Store.App.dto.user.request;

import com.Ali.Store.App.service.user.authentication.CommonFieldsForSavePeople;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class RegisterUserRequest implements CommonFieldsForSavePeople {
    @NotBlank(message = "Username must not be blank !")
    @Pattern(regexp = "^(09\\d{9}|[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+)$", message = "Username must be a valid email or phone number !")
    private String username;

    @NotBlank(message = "Password can't be empty !")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d@$%._*!?]{8,}$"
            , message = "Password must be combination of (capital letters, lowercase letters, number , special characters) !")
    private String password;
}
