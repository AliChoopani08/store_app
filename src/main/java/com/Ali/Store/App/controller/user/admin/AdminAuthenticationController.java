package com.Ali.Store.App.controller.user.admin;


import com.Ali.Store.App.dto.user.request.CreateAdminRequest;
import com.Ali.Store.App.security.jwt.JwtResponse;
import com.Ali.Store.App.service.user.authentication.AuthenticationServiceInterface;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.ResponseEntity.status;

@Tag(name = "Create Admin API", description = "This Operation related to create new admin")
@RestController
@RequestMapping("/admin/auth")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminAuthenticationController {

    private final AuthenticationServiceInterface service;

    @PostMapping("/register")
    @Operation(
            summary = "Create Another Admin",
            description = "Just an admin who has logged in can register another admin"
    )
    public ResponseEntity<JwtResponse> createAdminAccount(@RequestBody @Valid CreateAdminRequest createAdminRequest, HttpServletRequest request) {
        final String deviceId = request.getHeader("User-Agent");

        final JwtResponse jwtResponse = service.saveAdmin(createAdminRequest, deviceId);

        return status(CREATED)
                .body(jwtResponse);
    }
}
