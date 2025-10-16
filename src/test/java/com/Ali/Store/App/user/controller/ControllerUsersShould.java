package com.Ali.Store.App.user.controller;

import com.Ali.Store.App.dto.user.request.RefreshTokenRequest;
import com.Ali.Store.App.dto.user.request.UserRequest;
import com.Ali.Store.App.dto.user.response.ProfileResponse;
import com.Ali.Store.App.dto.user.response.UserResponse;
import com.Ali.Store.App.controller.user.AuthenticationController;
import com.Ali.Store.App.entities.userAndProfileUser.ProfileUser;
import com.Ali.Store.App.security.jwt.JwtResponse;
import com.Ali.Store.App.security.userDetails.UserDetailsImpl;
import com.Ali.Store.App.service.user.authentication.AuthenticationServiceInterface;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import static com.Ali.Store.App.entities.userAndProfileUser.Role.ROLE_USER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthenticationController.class)
@Import(ConfigTest.class)
public class ControllerUsersShould { // load controller and jwt filters

    @Autowired
    private AuthenticationServiceInterface service;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private JwtResponse jwtResponse;
    private String fakeRefreshToken;
    private String deviceId;

    @BeforeEach
    void setUp() {
        String fakeAccessToken = "GyuwgugdgsghsggdgsfgggdgyeFSFSF53654fdgd=j";
        fakeRefreshToken = "jdhgfggfgghbcdgfdreter534qr5gsgsgGSDSF5erVffdrst6";
        deviceId = "acer-315-55kg";

        final ProfileResponse profileUserDetails = new ProfileResponse(12L, "Ali choopani", "09213467845", null);
        UserResponse userInformationDetails = new UserResponse(12L, "09213467845", ROLE_USER.name(), profileUserDetails);

        jwtResponse = new JwtResponse(fakeAccessToken, fakeRefreshToken, userInformationDetails);
    }

    @Test
    void save_user() throws Exception {
        final UserRequest registerUserRequest = new UserRequest("09213467845", "AliCho34idj", deviceId);

         given(service.saveUser(any(UserRequest.class), any(String.class)))
                         .willReturn(jwtResponse);

        mockMvc.perform(post("/auth/register")
                        .header("User-Agent", "PostmanRuntime-acer315-55kg")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerUserRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$['Refresh Token']").exists())
                .andExpect(jsonPath("$['Access Token']").exists());
    }

    @Test
    void login_with_username() throws Exception {
        final UserRequest registerUserRequest = new UserRequest("09213467845", "AliCho34idj", "acer-315-55kg");

        given(service.login(any(UserRequest.class), any(String.class)))
                .willReturn(jwtResponse);

        mockMvc.perform(post("/auth/login")
                        .header("User-Agent", "PostmanRuntime-acer315-55kg")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerUserRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$['Refresh Token']").exists())
                .andExpect(jsonPath("$['Access Token']").exists());
    }

    @Test
    void create_new_access_token_with_refresh_token() throws Exception {
        final RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest(fakeRefreshToken, deviceId);

        given(service.createNewAccessToken(any(RefreshTokenRequest.class)))
                .willReturn(jwtResponse);

        mockMvc.perform(post("/auth/access/token")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshTokenRequest)))
                .andExpect(jsonPath("$['Refresh Token']").exists())
                .andExpect(jsonPath("$['Access Token']").exists());

    }

    @Test
    void logout() throws Exception {
        final ProfileUser profileUser = new ProfileUser("Ali Choopani", null, null, null);
        final UserDetailsImpl userInformationDetails =
                new UserDetailsImpl("09213467845", "AliCho34idj", List.of(new SimpleGrantedAuthority(ROLE_USER.name())), 12L, profileUser);

        willDoNothing().given(service).logout(any(Long.class));

        mockMvc.perform(delete("/auth")
                .with(user(userInformationDetails)))
                .andExpect(status().isNoContent());
    }
}
