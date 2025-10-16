package com.Ali.Store.App.dto.user.request;

import com.Ali.Store.App.service.user.authentication.CommonFieldsForSavePeople;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class UserRequest implements CommonFieldsForSavePeople {
    @NotBlank(message = "Username must not be blank !")
    @Pattern(regexp = "^(09\\d{9}|[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+)$", message = "Username must be a valid email or phone number !")
    private String username;

    @NotBlank(message = "Password is empty !")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d@$%._*!?]{8,}$"
            , message = "Password must be combination of (capital letters, lowercase letters, number , special characters) !")
    private String password;

    @NotBlank(message = "Device Id is empty !")
    @Size(min = 10, max = 500, message = "Device Id must be between 1 until 500 character !")
    @Schema(description = "Unique identification for each device of user -> (It must be made randomly by Front End)")
    private String deviceId;
}
