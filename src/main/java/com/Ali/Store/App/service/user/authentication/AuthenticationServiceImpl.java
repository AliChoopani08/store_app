package com.Ali.Store.App.service.user.authentication;

import com.Ali.Store.App.dto.security.PwdVerifyJwtResponse;
import com.Ali.Store.App.dto.user.*;
import com.Ali.Store.App.dto.user.request.*;
import com.Ali.Store.App.dto.user.response.UserSummary;
import com.Ali.Store.App.entities.userAndProfileUser.ProfileUser;
import com.Ali.Store.App.entities.userAndProfileUser.RefreshToken;
import com.Ali.Store.App.entities.userAndProfileUser.Role;
import com.Ali.Store.App.entities.userAndProfileUser.Users;
import com.Ali.Store.App.exceptions.security.ExpiredRefreshToken;
import com.Ali.Store.App.exceptions.DuplicateValueException;
import com.Ali.Store.App.exceptions.user.NotFoundUser;
import com.Ali.Store.App.repository.RefreshTokenRepository;
import com.Ali.Store.App.repository.userAndProfileUser.UserRepository;
import com.Ali.Store.App.security.customizationAuthentication.CustomAuthenticationToken;
import com.Ali.Store.App.dto.security.JwtAuthResponse;
import com.Ali.Store.App.security.jwt.JwtAuthServiceInterface;
import com.Ali.Store.App.security.jwt.JwtPwdVerifyServiceInterface;
import com.Ali.Store.App.security.refreshToken.RefreshTokenServiceInterface;
import com.Ali.Store.App.security.userDetails.UserDetailsImpl;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.Ali.Store.App.entities.userAndProfileUser.Role.ROLE_USER;

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
    private final RefreshTokenRepository repositoryRefreshToken;
    private final PasswordEncoder encoder;
    private final JwtPwdVerifyServiceInterface jwtPwdVerifyService;


    @Override
    @Transactional
    public JwtAuthResponse saveUser(UserRequest userRequest) {
        final Users user = userMapper.toEntity(userRequest);
        user.setRole(ROLE_USER);

        return helperForCommonCodesOfSavePerson(userRequest, user, userRequest.getDeviceId());
    }

    @Override
    @Transactional
    public JwtAuthResponse saveAdmin(CreateAdminRequest createAdminRequest, String deviceInfo) {
        final Users user = userMapper.createAdminRequestToUsers(createAdminRequest);
        user.setRole(Role.valueOf(createAdminRequest.getRole()));

        return helperForCommonCodesOfSavePerson(createAdminRequest, user, deviceInfo);
    }

    @Override
    public JwtAuthResponse login(UserRequest userRequest, String deviceInfo) {
        final Authentication authenticate = authenticationManager.authenticate(new CustomAuthenticationToken(userRequest.getUsername(), userRequest.getPassword(), userRequest.getDeviceId()));

        final UserDetailsImpl userDetails = (UserDetailsImpl) authenticate.getPrincipal();
        final Users user = userMapper.userDetailsImplToUsers(userDetails);

        final RefreshToken refreshToken = refreshTokenService.createRefreshToken(user, userRequest.getDeviceId(), deviceInfo);
        final String accessToken = jwtAuthService.generateAccessToken(user.getUsername());
        final UserSummary userResponse = userMapper.toSummary(user);

        return new JwtAuthResponse(refreshToken.getToken(), accessToken, userResponse);
    }


    @Override
    @Transactional
    public void logout(Long userId) {
        refreshTokenService.deleteByUser(getCurrentUserById(userId));
    }

    @Override
    @Transactional
    public UserSummary changeUsername(Long userId, ChangeUsernameRequest req) {
        final Users currentUser = getCurrentUserById(userId);

        currentUser.setUsername(req.getNewUsername());
        final Users savedUser = repository.save(currentUser);

        return userMapper.toSummary(savedUser);
    }

    @Override
    public PwdVerifyJwtResponse passwordVerify(Long userId, PasswordVerifyRequest passwordVerifyRequest) {
        final Users user = getCurrentUserById(userId);

        if (!encoder.matches(passwordVerifyRequest.currentPassword, user.getPassword())) {
            throw new BadCredentialsException("Password is invalid !");
        }
        final String createdToken = jwtPwdVerifyService.generatePwdVerificationToken(user.getUsername());

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

        return userMapper.toSummary(savedUser);
    }


    @Override
    @Transactional
    public JwtAuthResponse createNewAccessToken(RefreshTokenRequest tokenRequest) {
        if (!refreshTokenService.expiredRefreshToken(tokenRequest.getToken(), tokenRequest.getDeviceId())) {
            final RefreshToken foundRefreshToken = refreshTokenService.getByTokenAndDeviceId(tokenRequest.getToken(), tokenRequest.getDeviceId());
            final String createdAccessToken = jwtAuthService.generateAccessToken(foundRefreshToken.getUser().getUsername());
            final UserSummary userResponse = userMapper.toSummary(foundRefreshToken.getUser());
            return new JwtAuthResponse(foundRefreshToken.getToken(), createdAccessToken, userResponse);
        } else {
            refreshTokenService.deleteExpiredUser(tokenRequest);
            throw new ExpiredRefreshToken(tokenRequest.getToken());
        }
    }

    @Transactional
    private <T extends CommonFieldsForSavePeople> JwtAuthResponse helperForCommonCodesOfSavePerson(T registerRequest, Users user, String deviceInfo) {
        final String encodedPassword = passwordEncoder.encode(registerRequest.getPassword());

        user.setPassword(encodedPassword);
        user.setStatus(true);
        user.setProfileFields(ProfileUser.builder().build());
        repository.findByUsername(registerRequest.getUsername()).ifPresent(__ -> {
            throw new DuplicateValueException("This User is already registered ! ");
        });
        repositoryRefreshToken.findByDeviceId(registerRequest.getDeviceId()).ifPresent(__ -> {
            throw new DuplicateValueException("This device id is already exist !");
        });

        final Users savedUser = repository.save(user);

        final RefreshToken refreshToken = refreshTokenService.createRefreshToken(savedUser, registerRequest.getDeviceId(), deviceInfo);
        String accessToken = jwtAuthService.generateAccessToken(savedUser.getUsername());
        final UserSummary userResponse = userMapper.toSummary(savedUser);

        return new JwtAuthResponse(refreshToken.getToken(), accessToken, userResponse);
    }

    private Users getCurrentUserById(Long userId) {
        return repository.findById(userId).orElseThrow(() -> new NotFoundUser(userId));
    }
}
