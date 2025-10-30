package com.Ali.Store.App.service.user.authentication;

import com.Ali.Store.App.dto.security.PwdVerifyJwtResponse;
import com.Ali.Store.App.dto.user.*;
import com.Ali.Store.App.dto.user.request.*;
import com.Ali.Store.App.dto.user.response.ProfileResponse;
import com.Ali.Store.App.dto.user.response.UserResponse;
import com.Ali.Store.App.entities.userAndProfileUser.ProfileUser;
import com.Ali.Store.App.entities.userAndProfileUser.RefreshToken;
import com.Ali.Store.App.entities.userAndProfileUser.Role;
import com.Ali.Store.App.entities.userAndProfileUser.Users;
import com.Ali.Store.App.exceptions.security.ExpiredRefreshToken;
import com.Ali.Store.App.exceptions.DuplicateValueException;
import com.Ali.Store.App.exceptions.user.NotFoundUser;
import com.Ali.Store.App.repository.userAndProfileUser.RepositoryRefreshToken;
import com.Ali.Store.App.repository.userAndProfileUser.RepositoryUser;
import com.Ali.Store.App.security.customizationAuthentication.CustomAuthenticationToken;
import com.Ali.Store.App.dto.security.AuthJwtResponse;
import com.Ali.Store.App.security.jwt.JwtAuthServiceInterface;
import com.Ali.Store.App.security.jwt.JwtPwdVerifyServiceInterface;
import com.Ali.Store.App.security.refreshToken.RefreshTokenServiceInterface;
import com.Ali.Store.App.security.userDetails.UserDetailsImpl;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import static com.Ali.Store.App.entities.userAndProfileUser.Role.ROLE_USER;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationServiceInterface {


    private final RepositoryUser repository;
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtAuthServiceInterface jwtAuthService;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenServiceInterface refreshTokenService;
    private final RepositoryRefreshToken repositoryRefreshToken;
    private final PasswordEncoder encoder;
    private final JwtPwdVerifyServiceInterface jwtPwdVerifyService;


    @Override
    @Transactional
    public AuthJwtResponse saveUser(UserRequest userRequest, String deviceInfo) {
        final Users user = userMapper.userRequestToUser(userRequest);
        user.setRole(ROLE_USER);

        return helperForCommonCodesOfSavePerson(userRequest, user, deviceInfo);
    }

    @Override
    @Transactional
    public AuthJwtResponse saveAdmin(CreateAdminRequest createAdminRequest, String deviceInfo) {
        final Users user = userMapper.createAdminRequestToUsers(createAdminRequest);
        user.setRole(Role.valueOf(createAdminRequest.getRole()));

        return helperForCommonCodesOfSavePerson(createAdminRequest, user, deviceInfo);
    }

    @Override
    public AuthJwtResponse login(UserRequest userRequest, String deviceInfo) {
        final Authentication authenticate = authenticationManager
                .authenticate(new CustomAuthenticationToken(userRequest.getUsername(), userRequest.getPassword(), userRequest.getDeviceId()));

        final UserDetailsImpl userDetails = (UserDetailsImpl) authenticate.getPrincipal();
        final Users user = userMapper.userDetailsImplToUsers(userDetails);

        final RefreshToken refreshToken = refreshTokenService.createRefreshToken(user, userRequest.getDeviceId(), deviceInfo);
        final String accessToken = jwtAuthService.generateAccessToken(user.getUsername());
        final UserResponse userResponse = getUserResponse(user);
        return new AuthJwtResponse(refreshToken.getToken(), accessToken, userResponse);
    }


    @Override
    @Transactional
    public void logout(Long userId) {
        refreshTokenService.deleteByUser(getCurrentUserById(userId));
    }

    @Override
    @Transactional
    public UserResponse changeUsername(Long userId, ChangeUsernameRequest changeUsernameRequest) {
        final Users currentUser = getCurrentUserById(userId);

        currentUser.setUsername(changeUsernameRequest.getNewUsername());
        final Users savedUser = repository.save(currentUser);

        return getUserResponse(savedUser);
    }

    @Override
    public PwdVerifyJwtResponse passwordVerify(Long userId, PasswordVerifyRequest passwordVerifyRequest) {
        final Users user = getCurrentUserById(userId);

        if (!encoder.matches(passwordVerifyRequest.currentPassword, user.getPassword())) {
            throw new BadCredentialsException("Password is invalid !");
        }
        final String createdToken = jwtPwdVerifyService.generatePwdVerificationToken(user.getUsername());

        return new PwdVerifyJwtResponse(createdToken, getUserResponse(user));
    }

    @Override
    @Transactional
    public UserResponse passwordReset(PasswordResetRequest passwordResetRequest) {
            final String username = jwtPwdVerifyService.extractUsername(passwordResetRequest.getPasswordRestToken());
            final Users foundUser = repository.findByUsername(username)
                    .orElseThrow(() -> new NotFoundUser("This user with this username is not exist in database !"));

            final String encodedPassword = passwordEncoder.encode(passwordResetRequest.newPassword);
            foundUser.setPassword(encodedPassword);

            final Users savedUser = repository.save(foundUser);

            return getUserResponse(savedUser);
    }


    @Override
    @Transactional
    public AuthJwtResponse createNewAccessToken(RefreshTokenRequest rtRequest) {
        if (!refreshTokenService.expiredRefreshToken(rtRequest.getToken(), rtRequest.getDeviceId())) {
            final RefreshToken foundRefreshToken = refreshTokenService.getByTokenAndDeviceId(rtRequest.getToken(), rtRequest.getDeviceId());
            final String createdAccessToken = jwtAuthService.generateAccessToken(foundRefreshToken.getUser().getUsername());
            final UserResponse userResponse = getUserResponse(foundRefreshToken.getUser());
            return new AuthJwtResponse(foundRefreshToken.getToken(), createdAccessToken, userResponse);
        }
        else {
            refreshTokenService.deleteExpiredUser(rtRequest);
            throw new ExpiredRefreshToken("This refresh token is expired !");
        }
    }

    @Transactional
    private <T extends CommonFieldsForSavePeople> AuthJwtResponse helperForCommonCodesOfSavePerson(T registerRequest, Users user, String deviceInfo) {
        ProfileUser profile = new ProfileUser();
        final String encodedPassword = passwordEncoder.encode(registerRequest.getPassword());

        user.setPassword(encodedPassword);
        user.setProfileFields(profile);
        repository.findByUsername(registerRequest.getUsername()).ifPresent(_ -> {
            throw new DuplicateValueException("This Username is already registered ! ");
        });
        repositoryRefreshToken.findByDeviceId(registerRequest.getDeviceId())
                .ifPresent(_ -> {
                    throw new DuplicateValueException("This device id is already exist !");
                });

        final Users savedUser = repository.save(user);
        final UserResponse userResponse = getUserResponse(savedUser);

        final RefreshToken refreshToken = refreshTokenService.createRefreshToken(savedUser, registerRequest.getDeviceId(), deviceInfo);
        String accessToken = jwtAuthService.generateAccessToken(savedUser.getUsername());

        return new AuthJwtResponse(refreshToken.getToken(), accessToken, userResponse);
    }

    private Users getCurrentUserById(Long userId) {
        return repository.findById(userId)
                .orElseThrow(() -> new NotFoundUser("This user doesn't exist in database !"));
    }

    public UserResponse getUserResponse(Users savedUser) {
        final ProfileResponse profileResponse = userMapper.profileUserToProfileResponse(savedUser.getProfile());

        return new UserResponse(savedUser.getId()
                , savedUser.getUsername()
                , savedUser.getRole().name()
                , profileResponse);
    }
}
