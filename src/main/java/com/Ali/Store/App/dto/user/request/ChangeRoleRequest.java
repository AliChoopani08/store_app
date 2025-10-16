package com.Ali.Store.App.dto.user.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ChangeRoleRequest {

    @NotBlank(message = "Alternative Role mustn't be null")
    @Pattern(regexp = "^ROLE_(USER|ADMIN)$", message = "Role must be only ROLE_USER or ROLE_ADMIN")
    private String newRole;
}
