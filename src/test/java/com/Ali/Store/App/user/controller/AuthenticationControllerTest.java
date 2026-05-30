package com.Ali.Store.App.user.controller;

import com.Ali.Store.App.dto.user.request.LoginUserRequest;
import com.Ali.Store.App.dto.user.request.LogoutRequest;
import com.Ali.Store.App.dto.user.request.RefreshTokenRequest;
import com.Ali.Store.App.dto.user.request.RegisterUserRequest;
import com.Ali.Store.App.dto.user.response.UserSummary;
import com.Ali.Store.App.controller.user.AuthenticationController;
import com.Ali.Store.App.dto.security.AuthResponse;
import com.Ali.Store.App.security.userDetails.UserDetailsImpl;
import com.Ali.Store.App.testConfigs.TestSecurityConfig;
import com.Ali.Store.App.service.user.authentication.AuthenticationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static com.Ali.Store.App.testHelpers.GivenHelper.givenHelper;
import static com.Ali.Store.App.entities.userAndProfileUser.Role.ROLE_USER;
import static java.util.List.of;
import static java.util.UUID.randomUUID;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthenticationController.class)
@Import({ConfigUserControllerTest.class, TestSecurityConfig.class})
@ActiveProfiles("test")
public class AuthenticationControllerTest {

    private static final String DEVICE_UUID_HEADER_NAME = "X-Device-UUID";
    @Autowired
    private AuthenticationService service;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private AuthResponse jwtResponse;
    private UUID deviceUuid;

    @BeforeEach
    void setUp() {
        String fakeAccessToken = "fake.access.token";
        UUID fakeRefreshToken = randomUUID();
        deviceUuid = randomUUID();

        UserSummary userSummary = UserSummary.builder()
                .id(1L)
                .username("09213467890")
                .role(ROLE_USER.name())
                .build();

        jwtResponse = AuthResponse.builder()
                .accessToken(fakeAccessToken)
                .refreshToken(fakeRefreshToken)
                .userResponse(userSummary)
                .build();
    }

    @Test
    void shouldCreateUser_whenDoesNotExist() throws Exception {
        final RegisterUserRequest registerUserRequest = createUserRequest();

        givenHelper(() -> service.saveUser(any(RegisterUserRequest.class), anyString()), jwtResponse);

        mockMvc.perform(post("/auth/register")
                        .header("User-Agent", "PostmanRuntime-acer315-55kg")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerUserRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$['Refresh Token']").exists())
                .andExpect(jsonPath("$['Access Token']").exists());
    }

    @Test
    void shouldLoginUser_whenUsernameAndPasswordBeValid() throws Exception {
        final RegisterUserRequest loginRequest = createUserRequest();

        givenHelper(() -> service.login(any(LoginUserRequest.class), any(UUID.class)), jwtResponse);

        mockMvc.perform(post("/auth/login")
                        .header("User-Agent", "PostmanRuntime-acer315-55kg")
                        .header(DEVICE_UUID_HEADER_NAME, deviceUuid)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$['Refresh Token']").exists())
                .andExpect(jsonPath("$['Access Token']").exists());
    }

    @Test
    void shouldCreateAccessToken_whenRefreshTokenAndDeviceUuidBeValid() throws Exception {
        final RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest(randomUUID());

        givenHelper(() -> service.createNewAccessToken(any(UUID.class), any(RefreshTokenRequest.class)), jwtResponse);

        mockMvc.perform(post("/auth/access/token")
                        .header(DEVICE_UUID_HEADER_NAME, deviceUuid)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshTokenRequest)))
                .andExpect(jsonPath("$['Refresh Token']").exists())
                .andExpect(jsonPath("$['Access Token']").exists());

    }

    @Test
    void shouldLogout_whenUserLoggedIn() throws Exception {
        final UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("091234567890")
                .authorities(of(new SimpleGrantedAuthority(ROLE_USER.name())))
                .build();

        willDoNothing().given(service).logout(any(LogoutRequest.class));

        mockMvc.perform(delete("/auth/refresh-token")
                        .with(user(userDetails))
                        .header(DEVICE_UUID_HEADER_NAME, deviceUuid))
                .andExpect(status().isNoContent());
    }

    private RegisterUserRequest createUserRequest() {
        return RegisterUserRequest.builder()
                .username("09123467890")
                .password("Fake.password.123")
                .build();
    }
}
