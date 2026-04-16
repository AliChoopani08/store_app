package com.Ali.Store.App.controller.user;

import com.Ali.Store.App.dto.product.response.ApiResponse;
import com.Ali.Store.App.dto.security.PwdVerifyJwtResponse;
import com.Ali.Store.App.dto.user.request.*;
import com.Ali.Store.App.dto.user.response.UserSummary;
import com.Ali.Store.App.dto.security.JwtAuthResponse;
import com.Ali.Store.App.security.userDetails.UserDetailsImpl;
import com.Ali.Store.App.service.user.authentication.AuthenticationService;
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

    private final AuthenticationService service;


    @PostMapping("/register")
    @Operation(
            summary = "User registration",
            security = {@SecurityRequirement(name = "")}
    )
    public ResponseEntity<JwtAuthResponse> registerUser(@RequestBody @Valid UserRequest userRequest, HttpServletRequest request) {
        final String deviceInfo = request.getHeader("User-Agent");

        final JwtAuthResponse savedUserToken = service.saveUser(userRequest);

        return status(CREATED)
                .body(savedUserToken);
    }

    @PostMapping("/login")
    @Operation(
            summary = "User logon",
            security = {@SecurityRequirement(name = "")}
    )
    public ResponseEntity<JwtAuthResponse> loginWithUsername(@RequestBody UserRequest userRequest, HttpServletRequest request) {
        final String deviceInfo = request.getHeader("User-Agent");

        final JwtAuthResponse loggedInUserToken = service.login(userRequest, deviceInfo);

        return status(CREATED)
                .body(loggedInUserToken);
    }

    @PostMapping("/access/token")
    @Operation(
            summary = "Reconstruction The Expired Access Token",
            description = "Reconstruction the expired access token with entered refresh token",
            security = {@SecurityRequirement(name = "")}
    )
    public ResponseEntity<JwtAuthResponse> createNewAccessToken(@RequestBody @Valid RefreshTokenRequest tokenRequest) {
        final JwtAuthResponse createdNewAccessToken = service.createNewAccessToken(tokenRequest);

        return status(CREATED)
                .body(createdNewAccessToken);
    }

    @PatchMapping("/me/username")
    @Operation(
            summary = "Change The logged in User's Username"
    )
    public ResponseEntity<ApiResponse<UserSummary>> changeUsername(@AuthenticationPrincipal UserDetailsImpl currentUser, @RequestBody ChangeUsernameRequest usernameRequest) {
        final UserSummary updateUsernameResponse = service.changeUsername(currentUser.getId(), usernameRequest);

        return ok(new ApiResponse<>(200, "Username updated successfully", updateUsernameResponse));
    }

    @PostMapping("/me/password-verify")
    @Operation(
            summary = "Password Verification",
            description = "The first it confirms the previous user's password validity, " +
                    " Then it generates a password reset token."
    )
    public ResponseEntity<ApiResponse<PwdVerifyJwtResponse>> passwordVerify(@AuthenticationPrincipal UserDetailsImpl currentUser, @RequestBody PasswordVerifyRequest passwordVerifyRequest) {
        final PwdVerifyJwtResponse generatedToken = service.passwordVerify(currentUser.getId(), passwordVerifyRequest);

        return status(CREATED)
                .body(new ApiResponse<>(201, "The password reset token created successfully", generatedToken));
    }

    @PatchMapping("/me/password-reset")
    @Operation(
            summary = "Change The logged in User's Password"
    )
    public ResponseEntity<ApiResponse<UserSummary>> changePassword(@RequestBody PasswordResetRequest passwordResetRequest) {
        final UserSummary responseRestPassword = service.passwordReset(passwordResetRequest);

        return ok(new ApiResponse<>(200, "Password reset successfully.", responseRestPassword));
    }

    @DeleteMapping("/refresh-token")
    @Operation(
            summary = "Logout and delete the refresh token"
    )
    public ResponseEntity<Void> logout(@AuthenticationPrincipal UserDetailsImpl currentUser) {
        service.logout(currentUser.getId());

        return status(NO_CONTENT)
                .build();
    }
}
