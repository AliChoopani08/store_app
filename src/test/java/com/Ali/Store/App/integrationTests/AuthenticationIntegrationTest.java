package com.Ali.Store.App.integrationTests;

import com.Ali.Store.App.dto.user.request.ChangeUsernameRequest;
import com.Ali.Store.App.dto.user.request.PasswordResetRequest;
import com.Ali.Store.App.dto.user.request.PasswordVerifyRequest;
import com.Ali.Store.App.dto.user.request.RegisterUserRequest;
import com.Ali.Store.App.repository.userAndProfileUser.UserRepository;
import com.Ali.Store.App.testConfigs.TestConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestConfig.class)
class AuthenticationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository repositoryUser;

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String AUTHORIZATION = "Authorization";
    private static final String X_DEVICE_UUID = "X-Device-UUID";
    private String accessToken;
    private UUID deviceUuid;
    private RegisterUserRequest userRequest;

    @BeforeEach
    void registering_some_users() throws Exception {
        repositoryUser.deleteAll();

        userRequest = RegisterUserRequest.builder()
                .username("09876543210")
                .password("Mohammad12ch")
                .build();

        final String registerResponse = createUserAndAccessToken(userRequest);

        accessToken = objectMapper.readTree(registerResponse)
                .get("Access Token")
                .asText();

        deviceUuid = UUID.fromString(objectMapper.readTree(registerResponse)
                .get("device UUID")
                .asText());
    }

    @Test
    void shouldChangeUsername_whenUserHasLoggedIn() throws Exception {
        final ChangeUsernameRequest req = new ChangeUsernameRequest("chopaniali373@gmail.com");

        mockMvc.perform(patch("/auth/me/username")
                        .with(csrf())
                        .header(AUTHORIZATION, BEARER_PREFIX + accessToken)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Username updated successfully"))
                .andExpect(jsonPath("$.data.username").value("chopaniali373@gmail.com"));
    }

    @Test
    void shouldResetPassword_whenPreviousPasswordAndPasswordVerifyTokenBeValid() throws Exception {
        /*
        Generate reset password token after being valid the previous password
         */
        final PasswordVerifyRequest pwdVerifyRequest = new PasswordVerifyRequest("Mohammad12ch");
        final String passwordVerifyResponse = mockMvc.perform(post("/auth/me/password-verify")
                        .header(AUTHORIZATION, BEARER_PREFIX + accessToken)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pwdVerifyRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("The password reset token created successfully"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        /*
        Get password reset token from returned json file
         */
        final String resetPasswordToken = objectMapper.readTree(passwordVerifyResponse)
                .get("data").get("Password Verify Token")
                .asText();

        /*
        Change password with generated password reset token
         */
        final PasswordResetRequest pwdResetRequest = new PasswordResetRequest(resetPasswordToken, "Ali.123@");
        mockMvc.perform(patch("/auth/me/password-reset")
                        .header(AUTHORIZATION, BEARER_PREFIX + accessToken)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pwdResetRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password reset successfully."));
    }

    @Test
    void shouldLogoutUser_whenUserLoggedIn() throws Exception {
        mockMvc.perform(delete("/auth/refresh-token")
                        .header(AUTHORIZATION, BEARER_PREFIX + accessToken)
                        .header(X_DEVICE_UUID, deviceUuid))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldThrowException_whenUserHasAnActiveRefreshTokenAndWantsToCreateAnotherOne() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .header(X_DEVICE_UUID, deviceUuid)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("This Refresh Token is already active in database. Please generate a new Access Token with this refresh token."));
    }


    private String createUserAndAccessToken(RegisterUserRequest registerReq) throws Exception {
        return mockMvc.perform(post("/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$['Access Token']").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }


}
