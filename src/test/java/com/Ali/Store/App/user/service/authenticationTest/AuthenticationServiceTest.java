package com.Ali.Store.App.user.service.authenticationTest;

import com.Ali.Store.App.dto.security.PwdVerifyJwtResponse;
import com.Ali.Store.App.dto.user.*;
import com.Ali.Store.App.dto.user.request.ChangeUsernameRequest;
import com.Ali.Store.App.dto.user.request.PasswordResetRequest;
import com.Ali.Store.App.dto.user.request.PasswordVerifyRequest;
import com.Ali.Store.App.dto.user.request.UserRequest;
import com.Ali.Store.App.dto.user.response.ProfileSummary;
import com.Ali.Store.App.dto.user.response.UserSummary;
import com.Ali.Store.App.entities.userAndProfileUser.RefreshToken;
import com.Ali.Store.App.entities.userAndProfileUser.Users;
import com.Ali.Store.App.repository.RefreshTokenRepository;
import com.Ali.Store.App.repository.userAndProfileUser.UserRepository;
import com.Ali.Store.App.security.customizationAuthentication.CustomAuthenticationToken;
import com.Ali.Store.App.dto.security.JwtAuthResponse;
import com.Ali.Store.App.security.jwt.JwtAuthServiceInterface;
import com.Ali.Store.App.security.jwt.JwtPwdVerifyServiceInterface;
import com.Ali.Store.App.security.refreshToken.RefreshTokenServiceInterface;
import com.Ali.Store.App.security.userDetails.UserDetailsImpl;
import com.Ali.Store.App.service.user.authentication.AuthenticationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static com.Ali.Store.App.testHelpers.WhenHelper.whenHelper;
import static com.Ali.Store.App.entities.userAndProfileUser.Role.ROLE_USER;
import static java.util.List.of;
import static java.util.Optional.empty;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

    @Mock
    private UserRepository repository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtAuthServiceInterface jwtAuthService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private RefreshTokenServiceInterface refreshTokenService;
    @Mock
    private JwtPwdVerifyServiceInterface pwdVerifyService;
    @Mock
    private RefreshTokenRepository repositoryRefreshToken;
    @InjectMocks
    AuthenticationServiceImpl service;

    private UserRequest userRequest;
    private Users user;
    private JwtAuthResponse authTokenResponse;
    private RefreshToken refreshToken;
    private UserSummary userResponse;

    @BeforeEach
    void setUp() {
        userRequest = createUserRequest();
        user = createUser();

        ProfileSummary profileSummary = ProfileSummary.builder()
                .id(1L)
                .phoneNumber("09876543210")
                .build();
        userResponse = UserSummary.builder()
                .id(1L)
                .username("09876543210")
                .role(ROLE_USER.name())
                .profileSummary(profileSummary)
                .build();

        refreshToken = createRefreshToken();
        authTokenResponse = createAuthJwtAuthResponse(refreshToken.getToken(), userResponse);
    }

    @Test
    void shouldCreateUser_whenUserDoesNotExist() {
        final String fakeEncodedPassword = "fake.encoded.password";

        whenHelper(userMapper.toEntity(any(UserRequest.class)), user);
        whenHelper(passwordEncoder.encode(anyString()), fakeEncodedPassword);
        whenHelper(repository.findByUsername(anyString()), empty());
        whenHelper(repositoryRefreshToken.findByDeviceId(anyString()), empty());
        whenHelper(repository.save(any(Users.class)), user);
        whenHelper(refreshTokenService.createRefreshToken(any(Users.class), anyString(), anyString()), refreshToken);
        whenHelper(jwtAuthService.generateAccessToken(anyString()), authTokenResponse.getAccessToken());
        whenHelper(userMapper.toSummary(any(Users.class)), userResponse);

        final JwtAuthResponse savedUser = service.saveUser(userRequest);

        assertThat(savedUser)
                .extracting(JwtAuthResponse::getAccessToken, j -> j.getUserResponse().profileSummary().phoneNumber())
                .containsExactly("fake.access.token", "09876543210");
    }

    @Test
    void shouldLoginUser_whenUsernameAndPasswordAndDeviceIdBeValid() {
        final String deviceId = "fake.device.id.123";
        final UserDetailsImpl userDetails = createUserDetails();
        Authentication fakeAuth = createFakeAuthentication(userDetails);
        
        whenHelper(authenticationManager.authenticate(any(Authentication.class)), fakeAuth);
        when(userMapper.userDetailsImplToUsers(eq((UserDetailsImpl) fakeAuth.getPrincipal())))
                .thenReturn(user);
        whenHelper(jwtAuthService.generateAccessToken(anyString()), authTokenResponse.getAccessToken());
        whenHelper(refreshTokenService.createRefreshToken(any(Users.class), anyString(), anyString()), refreshToken);
        whenHelper(userMapper.toSummary(any(Users.class)), userResponse);

        final JwtAuthResponse loggedIn = service.login(userRequest, deviceId);

        assertThat(loggedIn)
                .extracting(JwtAuthResponse::getAccessToken, j -> j.getUserResponse().username())
                .containsExactly("fake.access.token", "09876543210");
    }

    private static UserDetailsImpl createUserDetails() {
        return UserDetailsImpl.builder()
                .id(1L)
                .username("09876543210")
                .authorities(of(new SimpleGrantedAuthority(ROLE_USER.name())))
                .build();
    }

    private static CustomAuthenticationToken createFakeAuthentication(UserDetailsImpl userDetails) {
        return new CustomAuthenticationToken(userDetails,
                empty(),
                userDetails.getAuthorities(),
                "fake.device.id.123");
    }

    @Test
    void shouldChangeUsername_whenUserExists() {
        final String newUsername = "new.username.of.user@gmail.com";
        final Long userId = user.getId();
        final ChangeUsernameRequest req = new ChangeUsernameRequest(newUsername);
        Users updatedUser = user.toBuilder()
                .username(newUsername)
                .build();
        UserSummary updatedResponse = userResponse.toBuilder()
                .username(newUsername)
                .build();

        whenHelper(repository.findById(anyLong()), Optional.of(user));
        whenHelper(repository.save(any(Users.class)), updatedUser);
        whenHelper(userMapper.toSummary(any(Users.class)), updatedResponse);
        final UserSummary changeUsernameResponse = service.changeUsername(userId, req);

        assertThat(changeUsernameResponse)
                .extracting(UserSummary::username, UserSummary::id)
                .containsExactly(newUsername, userId);
    }

    @Test
    void shouldGeneratePasswordVerifyToken_whenUserExistsAndSavedPasswordBeValid() {
        final Long userId = user.getId();
        final String username = user.getUsername();
        final PasswordVerifyRequest request = new PasswordVerifyRequest("Current.and.saved.password.of.user");
        String fakePwdVerifyToken = "fake.password.verify.token";

        whenHelper(repository.findById(anyLong()), Optional.of(user));
        whenHelper(passwordEncoder.matches(anyString(), anyString()), true);
        whenHelper(pwdVerifyService.generatePwdVerificationToken(anyString()), fakePwdVerifyToken);
        whenHelper(userMapper.toSummary(any(Users.class)), userResponse);

        final PwdVerifyJwtResponse generatedToken = service.passwordVerify(userId, request);

        assertThat(generatedToken)
                .extracting(PwdVerifyJwtResponse::getPasswordVerifyToken, p -> p.getUserResponse().username())
                .containsExactly(fakePwdVerifyToken, username);
    }

    @Test
    void shouldResetPassword_whenPasswordVerifyTokenBeValid() {
        final String newPassword = "New.password.123";
        final PasswordResetRequest passwordReset = new PasswordResetRequest("fake.password.verify.token", newPassword);
        String exceptedPasswordEncoded = "encoded.new.password";
        final Users updatedUser = user.toBuilder()
                        .password(newPassword)
                                .build();

        whenHelper(pwdVerifyService.extractUsername(anyString()), user.getUsername());
        whenHelper(repository.findByUsername(anyString()), Optional.of(user));
        whenHelper(passwordEncoder.encode(any(String.class)),exceptedPasswordEncoded);
        whenHelper(repository.save(any(Users.class)), updatedUser);
        whenHelper(userMapper.toSummary(any(Users.class)), userResponse);

        final UserSummary response = service.passwordReset(passwordReset);

        assertThat(response.username())
                .isEqualTo("09876543210");

    }

    private static RefreshToken createRefreshToken() {
        return RefreshToken.builder()
                .id(2L)
                .token("fake.refresh.token")
                .deviceId("fake.device.id.123")
                .build();
    }
    private static JwtAuthResponse createAuthJwtAuthResponse(String refreshToken, UserSummary response) {
        return JwtAuthResponse.builder()
                .accessToken("fake.access.token")
                .refreshToken(refreshToken)
                .userResponse(response)
                .build();
    }
    private static Users createUser() {
        return Users.builder()
                .id(1L)
                .username("09876543210")
                .password("Fake.password.123")
                .role(ROLE_USER)
                .build();
    }
    private static UserRequest createUserRequest() {
        return UserRequest.builder()
                .username("09876543210")
                .password("Fake.password.123")
                .deviceId("fake.device.id.123")
                .build();
    }
}
