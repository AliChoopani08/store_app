package com.Ali.Store.App.controller.user.admin;

import com.Ali.Store.App.dto.user.request.ChangeRoleRequest;
import com.Ali.Store.App.dto.user.request.SearchUserRequest;
import com.Ali.Store.App.dto.user.response.UserSummary;
import com.Ali.Store.App.service.admin.ManageUserServiceImpl;
import com.Ali.Store.App.service.admin.ManageUsersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;

@Tag(name = "Users Management API" ,description = "Operators related to user management by admin")
@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admin/user")
public class ManageUsersByAdmin {

    private final ManageUsersService service;

    public ManageUsersByAdmin(ManageUserServiceImpl service) {
        this.service = service;
    }

    @GetMapping
    @Operation(
            summary = "Search Users By Username",
            parameters = @Parameter(name = "username", description = "Including Email Or Phone Number", example = "09111222333")
    )
    public ResponseEntity<Page<UserSummary>> searchUsers(@ModelAttribute SearchUserRequest searchUserRequest,
                                                         @PageableDefault(sort = "id", direction = DESC)Pageable pageable) {

        return ok(service.searchUsers(searchUserRequest, pageable));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get User By Id"
    )
    public ResponseEntity<UserSummary> getById(@PathVariable Long id) {
        return ok(service.findUserById(id));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete User By id"
    )
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        service.disActiveUserById(id);

        return status(NO_CONTENT)
                .build();
    }

    @PatchMapping("/role/{id}")
    @Operation(
            summary = "User Role Change By Admin",
            parameters = {
                    @Parameter(name = "id", description = "Individual Id"),
                    @Parameter(name = "Alternative Role", description = "Including USER and ADMIN")
            }
    )
    public ResponseEntity<UserSummary> roleChange(@PathVariable Long id, @RequestBody @Valid ChangeRoleRequest changeRoleRequest) {
        return ok(service.changeRoleOfUser(id, changeRoleRequest));
    }
}
