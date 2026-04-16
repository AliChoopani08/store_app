package com.Ali.Store.App.user.controller;

import com.Ali.Store.App.dto.user.request.RefreshTokenRequest;
import com.Ali.Store.App.dto.user.request.UserRequest;
import com.Ali.Store.App.dto.user.response.UserSummary;
import com.Ali.Store.App.controller.user.AuthenticationController;
import com.Ali.Store.App.dto.security.JwtAuthResponse;
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

import static com.Ali.Store.App.testHelpers.GivenHelper.givenHelper;
import static com.Ali.Store.App.entities.userAndProfileUser.Role.ROLE_USER;
import static java.util.List.of;
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

    @Autowired
    private AuthenticationService service;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private JwtAuthResponse jwtResponse;
    private String fakeRefreshToken;
    private String deviceId;

    @BeforeEach
    void setUp() {
        String fakeAccessToken = "fake.access.token";
        fakeRefreshToken = "fake.refresh.token";
        deviceId = "fake.device.id";

        UserSummary userInformationDetails = UserSummary.builder()
                .id(1L)
                .username("09213467890")
                .role(ROLE_USER.name())
                .build();

        jwtResponse = JwtAuthResponse.builder()
                .accessToken(fakeAccessToken)
                .refreshToken(fakeRefreshToken)
                .userResponse(userInformationDetails)
                .build();
    }

    @Test
    void shouldCreateUser_whenDoesNotExist() throws Exception {
        final UserRequest registerUserRequest = createUserRequest();

        givenHelper(() -> service.saveUser(any(UserRequest.class)), jwtResponse);

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
        final UserRequest loginRequest = createUserRequest();

        givenHelper(() -> service.login(any(UserRequest.class), anyString()), jwtResponse);

        mockMvc.perform(post("/auth/login")
                        .header("User-Agent", "PostmanRuntime-acer315-55kg")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$['Refresh Token']").exists())
                .andExpect(jsonPath("$['Access Token']").exists());
    }

    @Test
    void shouldCreateAccessToken_whenRefreshTokenBeValid() throws Exception {
        final RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest(fakeRefreshToken, deviceId);

        givenHelper(() -> service.createNewAccessToken(any(RefreshTokenRequest.class)), jwtResponse);

        mockMvc.perform(post("/auth/access/token")
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

        willDoNothing().given(service).logout(anyLong());

        mockMvc.perform(delete("/auth/refresh-token")
                        .with(user(userDetails)))
                .andExpect(status().isNoContent());
    }

    private UserRequest createUserRequest() {
        return UserRequest.builder()
                .username("09123467890")
                .password("Fake.password.123")
                .deviceId("fake.device.id")
                .build();
    }
}
