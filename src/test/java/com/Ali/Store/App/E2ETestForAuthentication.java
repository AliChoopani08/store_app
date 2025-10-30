package com.Ali.Store.App;

import com.Ali.Store.App.dto.user.request.ChangeUsernameRequest;
import com.Ali.Store.App.dto.user.request.PasswordResetRequest;
import com.Ali.Store.App.dto.user.request.PasswordVerifyRequest;
import com.Ali.Store.App.dto.user.request.UserRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(PER_CLASS)
@ActiveProfiles("test")
@Import(TestJpaAuditingConfig.class)
class E2ETestForAuthentication {


	private String jwt;
    @Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;



	@BeforeAll
	void registering_some_users() throws Exception {

		final String registerResponse = getRegisteredUser();

		jwt = objectMapper.readTree(registerResponse)
				.get("Access Token")
				.asText();
	}

	/**
	 * It will throw the DuplicateValueException,<br>
	 * 			Because we created a new user recently, and they have an active Refresh Token.<br>
	 * However, when this user doesn't have an active Refresh Token, this test will pass.
	 */
	@Test
	void login_request() throws Exception { //
		final UserRequest loginRequest = new UserRequest("09330825477", "Mohammad12ch", "Android-Iphone-13-pro");

		mockMvc.perform(post("/auth/login")
				     .contentType(APPLICATION_JSON)
				     .content(objectMapper.writeValueAsString(loginRequest)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$['Access Token']").exists());

	}

	@Test
	void change_username() throws Exception {
		final ChangeUsernameRequest usernameRequest = new ChangeUsernameRequest("chopaniali373@gmail.com");

		mockMvc.perform(patch("/auth/me/username")
						.header("Authorization", "Bearer " + jwt)
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(usernameRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.username").value("chopaniali373@gmail.com"));
	}

	@Test
	void reset_password() throws Exception {
		final PasswordVerifyRequest pwdVerifyRequest = new PasswordVerifyRequest("Mohammad12ch");
		final String responsePwdVerifyEndpoint = mockMvc.perform(post("/auth/me/password-reset-token")
						.header("Authorization", "Bearer " + jwt)
						.contentType(APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(pwdVerifyRequest)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.message").value("The password reset token created successfully"))
				.andReturn()
				.getResponse()
				.getContentAsString();

		final String token = objectMapper.readTree(responsePwdVerifyEndpoint)
				.get("data").get("Password Verify Token")
				.asText();

		final PasswordResetRequest pwdResetRequest = new PasswordResetRequest(token, "Ali.123@");
		mockMvc.perform(patch("/auth/me/password")
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(pwdResetRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message").value("Password reset successfully."));
	}

	@Test
	void logout() throws Exception {
		mockMvc.perform(delete("/auth")
				.header("Authorization", "Bearer " + jwt))
				.andExpect(status().isNoContent());
	}


		private String getRegisteredUser() throws Exception {
			final UserRequest registerRequest = new UserRequest("09330825477", "Mohammad12ch", "Android-Iphone-13-pro");

			return mockMvc.perform(post("/auth/register")
							.contentType(APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(registerRequest)))
					.andExpect(status().isCreated())
					.andExpect(jsonPath("$['Access Token']").exists())
					.andReturn()
					.getResponse()
					.getContentAsString();
		}

	@Test
	void throw_exception_when_the_refresh_token_is_active_and_user_wants_to_login() throws Exception {
		final UserRequest registerRequest = new UserRequest("09330825477", "Mohammad12ch", "Android-Iphone-13-pro");

		 mockMvc.perform(post("/auth/login")
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(registerRequest)))
				 .andExpect(status().isConflict())
				 .andExpect(jsonPath("$.message").value("This Refresh Token is already active in database. Please generate a new Access Token with this refresh token."));
	}
}
