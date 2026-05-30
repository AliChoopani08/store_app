package com.Ali.Store.App.controller.user;

import com.Ali.Store.App.dto.product.response.ApiResponse;
import com.Ali.Store.App.dto.user.request.ProfileRequest;
import com.Ali.Store.App.dto.user.response.UserSummary;
import com.Ali.Store.App.security.userDetails.UserDetailsImpl;
import com.Ali.Store.App.service.user.account.UserAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import static org.springframework.http.ResponseEntity.noContent;
import static org.springframework.http.ResponseEntity.ok;

@Tag(name = "Profile API", description = "Operations related to manage profile by user")
@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
@Slf4j
public class ManagementProfileController {

    private final UserAccountService service;


    @PutMapping
    @Operation(
            summary = "Update All Of Profile Fields With Inserted Fields",
            description = "If each one of request fields be null, previous saved value doesn't change"
    )
    public ResponseEntity<ApiResponse<UserSummary>> updateUser(@AuthenticationPrincipal UserDetailsImpl currentUser, @RequestBody @Valid ProfileRequest profileRequest) {
        log.info("API request: update all fields of profile of user [{}]...", currentUser.getId());

        final UserSummary updatedUser = service.updateProfile(currentUser.getId(), profileRequest);

        return ok(new ApiResponse<>(200, "Your profile fields updated successfully",updatedUser));
    }

    @DeleteMapping("/account")
    @Operation(
            summary = "Disable the Activated Account By User Who Has Been Logged In"
    )
    public ResponseEntity<Void> disableAccount(@AuthenticationPrincipal UserDetailsImpl currentUser) {
        log.info("API request: disable the user [{}] account...", currentUser.getId());

        service.disableAccount(currentUser.getId());

        return noContent().build();
    }

    @GetMapping("/me")
    @Operation(
            summary = "Display Of Profile User Who Logged In"
    )
    public ResponseEntity<UserSummary> showMyProfile(@AuthenticationPrincipal UserDetailsImpl currentUser) {

        return ok(service.displayProfile(currentUser.getId()));
    }
}
