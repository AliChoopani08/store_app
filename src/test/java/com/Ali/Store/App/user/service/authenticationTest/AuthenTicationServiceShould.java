package com.Ali.Store.App.user.service.authenticationTest;

import com.Ali.Store.App.dto.security.PwdVerifyJwtResponse;
import com.Ali.Store.App.dto.user.*;
import com.Ali.Store.App.dto.user.request.ChangeUsernameRequest;
import com.Ali.Store.App.dto.user.request.PasswordResetRequest;
import com.Ali.Store.App.dto.user.request.PasswordVerifyRequest;
import com.Ali.Store.App.dto.user.request.UserRequest;
import com.Ali.Store.App.dto.user.response.UserResponse;
import com.Ali.Store.App.entities.userAndProfileUser.ProfileUser;
import com.Ali.Store.App.entities.userAndProfileUser.RefreshToken;
import com.Ali.Store.App.entities.userAndProfileUser.Users;
import com.Ali.Store.App.repository.userAndProfileUser.RepositoryUser;
import com.Ali.Store.App.security.customizationAuthentication.CustomAuthenticationToken;
import com.Ali.Store.App.dto.security.AuthJwtResponse;
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
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import static com.Ali.Store.App.entities.userAndProfileUser.Role.ROLE_USER;
import static java.time.Duration.ofDays;
import static java.time.Instant.now;
import static java.util.Optional.ofNullable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenTicationServiceShould {

    @Mock
    private RepositoryUser repository;
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
    @InjectMocks
    AuthenticationServiceImpl service;

    private UserRequest userRequest;
    private Users user;
    private AuthJwtResponse fakeToken;
    private RefreshToken refreshToken;

    @BeforeEach
    void setUp() {
        userRequest = new UserRequest("09214893654", "Alifghd@54t", "IOS-16-pro-max");
        user = new Users("09214893654", "Alifghd@54t");
        user.setId(12L);
        user.setRole(ROLE_USER);
        fakeToken = new AuthJwtResponse("hagdgfghhgyueegysgfsGDGYUAGGGGFGG545736.fake");
        refreshToken = new RefreshToken(3L,"HSGYGUgygdggdh-yyr5t536()ksh","Windows-11-User-1234","Windows-11-User",now().plus(ofDays(20)));
    }

    @Test
    void save_user_with_phone() {
        final String fakeEncodedPassword = "Shuhduh5463tdgsvsvgsgvxgsvgvgsvggsv";
        String deviceInfo = "Windows-11-User";

        when(userMapper.userRequestToUser(any(UserRequest.class)))
                .thenReturn(user);
        when(passwordEncoder.encode(any(String.class)))
                .thenReturn(fakeEncodedPassword);
        when(repository.findByUsername(any(String.class)))
                .thenReturn(Optional.empty());
        when(repository.save(any(Users.class)))
                .thenReturn(user);
        when(refreshTokenService.createRefreshToken(any(Users.class), any(String.class), any(String.class)))
                .thenReturn(refreshToken);
        when(jwtAuthService.generateAccessToken(user.getUsername()))
                .thenReturn(fakeToken.getAccessToken());
        final AuthJwtResponse savedUser = service.saveUser(userRequest, deviceInfo);

        assertThat(savedUser)
                .isEqualTo(fakeToken);
    }

    @Test
    void login_with_username() {
        final ProfileUser profileUser = new ProfileUser("Mohammad", null, null, LocalDate.of(1992, 9, 12));
        final UserDetailsImpl userDetails = new UserDetailsImpl("09214893654", "Alifghd@54t", List.of(new SimpleGrantedAuthority("ROLE_USER")), 12L, profileUser);
        String deviceInfo = "Android-13-Xiaomi";
        Authentication fakeAuth = new CustomAuthenticationToken(userDetails, userDetails.getUsername(), userDetails.getAuthorities(), "Android-13-Xiaomi");

        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenReturn(fakeAuth);
        when(userMapper.userDetailsImplToUsers(any(UserDetailsImpl.class)))
                .thenReturn(user);
        when(jwtAuthService.generateAccessToken(user.getUsername()))
                .thenReturn(fakeToken.getAccessToken());
        when(refreshTokenService.createRefreshToken(any(Users.class), any(String.class), any(String.class)))
                .thenReturn(refreshToken);
        final AuthJwtResponse loggedIn = service.login(userRequest, deviceInfo);

        assertThat(loggedIn).isEqualTo(fakeToken);
    }

    @Test
    void change_username() {
        final ChangeUsernameRequest usernameRequest = new ChangeUsernameRequest("ali.23.choupani@gmail.com");
        Users updatedUser = new Users("ali.23.choupani@gmail.com", "Alifghd@54t");
        updatedUser.setId(12L);
        updatedUser.setRole(ROLE_USER);

        when(repository.findById(any(Long.class)))
                .thenReturn(ofNullable(user));
        when(repository.save(any(Users.class)))
                .thenReturn(updatedUser);

        final UserResponse changeUsernameResponse = service.changeUsername(12L, usernameRequest);

        assertThat(changeUsernameResponse)
                .extracting(UserResponse::username, UserResponse::id)
                .containsExactly("ali.23.choupani@gmail.com", 12L);
    }

    @Test
    void password_verify_and_generate_pwd_verify_token() {
        final PasswordVerifyRequest request = new PasswordVerifyRequest("Alifghd@54t");
        String fakePwdVerifyToken = "gdhgsterteuhHSG4df34@fsrdferdhetd53";

        when(repository.findById(any(Long.class)))
                .thenReturn(ofNullable(user));
        when(passwordEncoder.matches(any(String.class), any(String.class)))
                .thenReturn(true);
        when(pwdVerifyService.generatePwdVerificationToken(any(String.class)))
                .thenReturn(fakePwdVerifyToken);

        final PwdVerifyJwtResponse result = service.passwordVerify(12L, request);

        assertThat(result)
                .extracting(PwdVerifyJwtResponse::getPasswordVerifyToken, p -> p.getUserResponse().username())
                .containsExactly("gdhgsterteuhHSG4df34@fsrdferdhetd53","09214893654");
    }

    @Test
    void password_reset() {
        final PasswordResetRequest passwordReset = new PasswordResetRequest("gdhgsterteuhHSG4df34@fsrdferdhetd53","Ali132345@h");
        String exceptedPasswordEncoded = "ksksjdFSGS4erdfxgxhdjsn635";
        final Users exceptedUser = new Users("09214893654", "Ali132345@h");
        exceptedUser.setRole(ROLE_USER);

        when(pwdVerifyService.extractUsername(any(String.class)))
                .thenReturn("09214893654");
        when(repository.findByUsername(any(String.class)))
                .thenReturn(ofNullable(user));
        when(passwordEncoder.encode(any(String.class)))
                .thenReturn(exceptedPasswordEncoded);
        when(repository.save(any(Users.class)))
                .thenReturn(exceptedUser);

        service.passwordReset(passwordReset);

        assertThat(user.getPassword())
                .isEqualTo(exceptedPasswordEncoded);
    }
}
