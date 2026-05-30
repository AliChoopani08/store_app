package com.Ali.Store.App.service.user.authentication;

import com.Ali.Store.App.dto.security.PwdVerifyJwtResponse;
import com.Ali.Store.App.dto.user.*;
import com.Ali.Store.App.dto.user.request.*;
import com.Ali.Store.App.dto.user.response.UserSummary;
import com.Ali.Store.App.entities.userAndProfileUser.*;
import com.Ali.Store.App.exceptions.security.ExpiredRefreshToken;
import com.Ali.Store.App.exceptions.security.NotFoundRefreshToken;
import com.Ali.Store.App.exceptions.user.DuplicateUsername;
import com.Ali.Store.App.exceptions.user.NotFoundUser;
import com.Ali.Store.App.repository.RefreshTokenRepository;
import com.Ali.Store.App.repository.userAndProfileUser.UserRepository;
import com.Ali.Store.App.security.customizationAuthentication.CustomAuthenticationToken;
import com.Ali.Store.App.dto.security.AuthResponse;
import com.Ali.Store.App.security.jwt.JwtAuthServiceInterface;
import com.Ali.Store.App.security.jwt.JwtPwdVerifyServiceInterface;
import com.Ali.Store.App.service.refreshToken.RefreshTokenServiceInterface;
import com.Ali.Store.App.security.userDetails.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.Ali.Store.App.entities.userAndProfileUser.Role.ROLE_USER;
import static com.Ali.Store.App.entities.userAndProfileUser.Role.valueOf;
import static java.util.UUID.randomUUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {


    private final UserRepository repository;
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtAuthServiceInterface jwtAuthService;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenServiceInterface refreshTokenService;
    private final PasswordEncoder encoder;
    private final JwtPwdVerifyServiceInterface jwtPwdVerifyService;
    private final RefreshTokenRepository refreshTokenRepository;


    @Override
    @Transactional
    public AuthResponse saveUser(RegisterUserRequest userRequest, String deviceInfo) {
        final Users user = userMapper.toEntity(userRequest);
        user.setRole(ROLE_USER);

        final AuthResponse authResponse = helperForCommonCodesOfSavePerson(userRequest, user, deviceInfo);

        log.info("A new user [{}] registered successfully", authResponse.getUserResponse().id());
        return authResponse;
    }

    @Override
    @Transactional
    public AuthResponse saveAdmin(CreateAdminRequest createAdminRequest, String deviceInfo) {
        final Users user = userMapper.createAdminRequestToUsers(createAdminRequest);
        user.setRole(valueOf(createAdminRequest.getRole()));

        final AuthResponse authResponse = helperForCommonCodesOfSavePerson(createAdminRequest, user, deviceInfo);

        log.info("A new admin [{}] created successfully", authResponse.getUserResponse().id());
        return authResponse;
    }

    @Override
    public AuthResponse login(LoginUserRequest loginUserRequest, UUID deviceUuid) {
        final Authentication authenticate = authenticationManager.authenticate(new CustomAuthenticationToken(loginUserRequest.getUsername(), loginUserRequest.getPassword(), deviceUuid));

        final UserDetailsImpl userDetails = (UserDetailsImpl) authenticate.getPrincipal();
        final Users user = userMapper.userDetailsImplToUsers(userDetails);

        final RefreshToken refreshToken = refreshTokenService.createRefreshToken(deviceUuid, user.getId());
        final String accessToken = jwtAuthService.generateAccessToken(user.getUsername());
        final UserSummary userResponse = userMapper.toSummary(user);

        return new AuthResponse(refreshToken.getToken(), accessToken, userResponse, deviceUuid);
    }


    @Override
    @Transactional
    public void logout(LogoutRequest logoutRequest) {
        refreshTokenService.removeRefreshTokenByUserIdAndDeviceUuid(logoutRequest);

        log.info("User [{}] logout and their refresh token deleted successfully", logoutRequest.getUserId());
    }

    @Override
    @Transactional
    public UserSummary changeUsername(Long userDetails, ChangeUsernameRequest req) {
        final Users user = getCurrentUserById(userDetails);

        user.setUsername(req.getNewUsername());
        final Users savedUser = repository.save(user);

        log.info("User [{}] username changed successfully", user);
        return userMapper.toSummary(savedUser);
    }

    @Override
    public PwdVerifyJwtResponse passwordVerifyAndGenerateAPasswwordVerifyToken(Long userId, PasswordVerifyRequest passwordVerifyRequest) {
        final Users user = getCurrentUserById(userId);

        if (!encoder.matches(passwordVerifyRequest.currentPassword, user.getPassword())) {
            throw new BadCredentialsException("Password is invalid !");
        }
        final String createdToken = jwtPwdVerifyService.generatePwdVerificationToken(user.getUsername());

        log.info("A new password verification token for user [{}] created successfully", user.getId());
        return new PwdVerifyJwtResponse(createdToken, userMapper.toSummary(user));
    }

    @Override
    @Transactional
    public UserSummary passwordReset(PasswordResetRequest passwordResetRequest) {
        final String username = jwtPwdVerifyService.extractUsername(passwordResetRequest.getPasswordRestToken());
        final Users foundUser = repository.findByUsername(username)
                .orElseThrow(() -> new NotFoundUser(username));

        final String encodedPassword = passwordEncoder.encode(passwordResetRequest.newPassword);
        foundUser.setPassword(encodedPassword);

        final Users savedUser = repository.save(foundUser);

        log.info("User [{}] password changed successfully", savedUser.getId());
        return userMapper.toSummary(savedUser);
    }


    @Override
    @Transactional
    public AuthResponse createNewAccessToken(UUID deviceUuid, RefreshTokenRequest tokenRequest) {
        final RefreshToken foundRefreshToken = refreshTokenRepository.findByTokenAndDeviceUuid(tokenRequest.getToken(), deviceUuid)
                .orElseThrow(NotFoundRefreshToken::new);

        if (!refreshTokenService.expiredRefreshToken(foundRefreshToken)) {
            final Users user = foundRefreshToken.getDevice()
                    .getUser();

            final String createdAccessToken = jwtAuthService.generateAccessToken(user.getUsername());
            final UserSummary userResponse = userMapper.toSummary(user);
            final AuthResponse authResponse = new AuthResponse(foundRefreshToken.getToken(), createdAccessToken, userResponse, deviceUuid);
            log.info("A new Access token for user [{}] created successfully", userResponse.id());
            return authResponse;
        } else {
            refreshTokenService.deleteExpiredRefreshTokenByDeviceUUid(foundRefreshToken);
            log.info("The expired refresh token [{}] deleted successfully", foundRefreshToken.getId());
            throw new ExpiredRefreshToken(foundRefreshToken.getToken());
        }
    }

    @Transactional
    private <T extends CommonFieldsForSavePeople> AuthResponse helperForCommonCodesOfSavePerson(T registerRequest, Users user, String deviceInfo) {
        repository.findByUsername(registerRequest.getUsername()).ifPresent(__ -> {
            throw new DuplicateUsername(registerRequest.getUsername());
        });

        final String encodedPassword = passwordEncoder.encode(registerRequest.getPassword());
        Device device;

        user.setPassword(encodedPassword);
        user.setStatus(true);
        user.setProfileFields(ProfileUser.builder().build());
        device = Device.builder()
                .deviceUuid(randomUUID())
                .deviceInfo(deviceInfo)
                .isAvailable(true)
                .build();
        user.addDevice(device);

        final Users savedUser = repository.save(user);

        final RefreshToken refreshToken = refreshTokenService.createRefreshToken(device.getDeviceUuid(), savedUser.getId());
        String accessToken = jwtAuthService.generateAccessToken(savedUser.getUsername());
        final UserSummary userResponse = userMapper.toSummary(savedUser);

        return new AuthResponse(refreshToken.getToken(), accessToken, userResponse, device.getDeviceUuid());
    }

    private Users getCurrentUserById(Long userId) {
        return repository.findById(userId)
                .orElseThrow(() -> new NotFoundUser(userId));
    }
}
