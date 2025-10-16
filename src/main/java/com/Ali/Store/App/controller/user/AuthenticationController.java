package com.Ali.Store.App.controller.user;

import com.Ali.Store.App.dto.user.request.RefreshTokenRequest;
import com.Ali.Store.App.dto.user.request.UserRequest;
import com.Ali.Store.App.security.jwt.JwtResponse;
import com.Ali.Store.App.security.userDetails.UserDetailsImpl;
import com.Ali.Store.App.service.user.authentication.AuthenticationServiceInterface;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.ResponseEntity.*;

@Tag(name = "User|Admin Auth API", description = "Operations related to individual authentication")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationServiceInterface service;


    @PostMapping("/register")
    @Operation(
            summary = "User registration",
            security = {@SecurityRequirement(name = "")}
    )
    public ResponseEntity<JwtResponse> registerUser(@RequestBody @Valid UserRequest userRequest, HttpServletRequest request) {
        final String deviceInfo = request.getHeader("User-Agent");

        final JwtResponse savedUserToken = service.saveUser(userRequest, deviceInfo);

        return status(CREATED)
                .body(savedUserToken);
    }

    @PostMapping("/login")
    @Operation(
            summary = "User logon",
            security = {@SecurityRequirement(name = "")}
    )
    public ResponseEntity<JwtResponse> loginWithUsername(@RequestBody UserRequest userRequest, HttpServletRequest request) {
        final String deviceInfo = request.getHeader("User-Agent");

        final JwtResponse loggedInUserToken = service.login(userRequest, deviceInfo);

        return status(CREATED)
                .body(loggedInUserToken);
    }

    @PostMapping("/access/token")
    @Operation(
            summary = "Reconstruction The Expired Access Token",
            description = "Reconstruction the expired access token with entered refresh token",
            security = {@SecurityRequirement(name = "")}
    )
    public ResponseEntity<JwtResponse> createNewAccessToken(@RequestBody @Valid RefreshTokenRequest tokenRequest) {
        final JwtResponse createdNewAccessToken = service.createNewAccessToken(tokenRequest);

        return status(CREATED)
                .body(createdNewAccessToken);
    }

    @DeleteMapping
    @Operation(
            summary = "Logout and delete the refresh token"
    )
    public ResponseEntity<Void> logout(@AuthenticationPrincipal UserDetailsImpl currentUser) {
        service.logout(currentUser.getId());

       return status(NO_CONTENT)
                .build();
    }
}
