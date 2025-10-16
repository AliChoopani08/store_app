package com.Ali.Store.App.controller.user;

import com.Ali.Store.App.dto.user.request.ProfileRequest;
import com.Ali.Store.App.dto.user.response.UserResponse;
import com.Ali.Store.App.security.userDetails.UserDetailsImpl;
import com.Ali.Store.App.service.user.account.UserAccountServiceInterface;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import static org.springframework.http.ResponseEntity.noContent;
import static org.springframework.http.ResponseEntity.ok;

@Tag(name = "Profile API", description = "Operations related to manage profile by user")
@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserAccountServiceInterface service;


    @PutMapping
    @Operation(
            summary = "Update All Of Profile Fields With Inserted Fields",
            description = "If each one of request fields be null, previous saved value doesn't change"
    )
    public ResponseEntity<UserResponse> updateUser(@AuthenticationPrincipal UserDetailsImpl currentUser, @RequestBody @Valid ProfileRequest profileRequest) {
        final UserResponse updatedUser = service.updateProfile(currentUser.getId(), profileRequest);

        return ok(updatedUser);
    }

    @DeleteMapping("/account")
    @Operation(
            summary = "Delete Account By User Who Logged In"
    )
    public ResponseEntity<Void> deleteAccount(@AuthenticationPrincipal UserDetailsImpl currentUser) {
        service.deleteAccount(currentUser.getId());

        return noContent().build();
    }

    @GetMapping("/me")
    @Operation(
            summary = "Display Of Profile User Who Logged In"
    )
    public ResponseEntity<UserResponse> showMyProfile(@AuthenticationPrincipal UserDetailsImpl currentUser) {

        return ok(service.displayProfile(currentUser.getId()));
    }
}
