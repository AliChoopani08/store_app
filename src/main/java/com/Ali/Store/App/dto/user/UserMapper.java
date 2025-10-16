package com.Ali.Store.App.dto.user;

import com.Ali.Store.App.dto.user.request.CreateAdminRequest;
import com.Ali.Store.App.dto.user.request.ProfileRequest;
import com.Ali.Store.App.dto.user.request.UserRequest;
import com.Ali.Store.App.dto.user.response.ProfileResponse;
import com.Ali.Store.App.dto.user.response.UserResponse;
import com.Ali.Store.App.entities.userAndProfileUser.ProfileUser;
import com.Ali.Store.App.entities.userAndProfileUser.Users;
import com.Ali.Store.App.security.userDetails.UserDetailsImpl;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = IGNORE)
@Component("userMapper")
public interface UserMapper {

    Users userRequestToUser(UserRequest userRequest);

    UserResponse usersToUserResponse(Users user);

    ProfileResponse profileUserToProfileResponse(ProfileUser profileUser);

    Users createAdminRequestToUsers(CreateAdminRequest createAdminRequest);

    @Mapping(target = "id", ignore = true)
    ProfileUser update(@MappingTarget ProfileUser oldProfile, ProfileRequest profileRequest);


    @Mapping(target = "role", expression = "java(userDetails.getAuthorities().isEmpty() ? null : " +
            "Role.valueOf(userDetails.getAuthorities().iterator().next().getAuthority()))")
    @Mapping(source = "profileUser", target = "profile")
    Users userDetailsImplToUsers(UserDetailsImpl userDetails);
}
