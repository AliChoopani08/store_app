package com.Ali.Store.App.user.service.authenticationTest;

import com.Ali.Store.App.dto.user.*;
import com.Ali.Store.App.dto.user.request.UserRequest;
import com.Ali.Store.App.entities.userAndProfileUser.ProfileUser;
import com.Ali.Store.App.entities.userAndProfileUser.RefreshToken;
import com.Ali.Store.App.entities.userAndProfileUser.Users;
import com.Ali.Store.App.repository.userAndProfileUser.RepositoryUser;
import com.Ali.Store.App.security.customizationAuthentication.CustomAuthenticationToken;
import com.Ali.Store.App.security.jwt.JwtResponse;
import com.Ali.Store.App.security.jwt.JwtServiceInterface;
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
    private JwtServiceInterface jwtService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private RefreshTokenServiceInterface refreshTokenService;
    @InjectMocks
    AuthenticationServiceImpl service;

    private UserRequest userRequest;
    private Users user;
    private JwtResponse fakeToken;
    private RefreshToken refreshToken;

    @BeforeEach
    void setUp() {
        userRequest = new UserRequest("09214893654", "Alifghd@54t", "IOS-16-pro-max");
        user = new Users("09214893654", "Alifghd@54t");
        user.setId(12L);
        user.setRole(ROLE_USER);
        fakeToken = new JwtResponse("hagdgfghhgyueegysgfsGDGYUAGGGGFGG545736.fake");
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
        when(jwtService.generateToken(user.getUsername()))
                .thenReturn(fakeToken.getAccessToken());
        final JwtResponse savedUser = service.saveUser(userRequest, deviceInfo);

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
        when(jwtService.generateToken(user.getUsername()))
                .thenReturn(fakeToken.getAccessToken());
        when(refreshTokenService.createRefreshToken(any(Users.class), any(String.class), any(String.class)))
                .thenReturn(refreshToken);
        final JwtResponse loggedIn = service.login(userRequest, deviceInfo);

        assertThat(loggedIn).isEqualTo(fakeToken);
    }
}
