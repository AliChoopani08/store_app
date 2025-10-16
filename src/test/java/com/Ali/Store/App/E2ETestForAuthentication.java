package com.Ali.Store.App;

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

	@Test
	void login_request() throws Exception {
		final UserRequest loginRequest = new UserRequest("09330825477", "Mohammad12ch", "Android-Iphone-13-pro");

		mockMvc.perform(post("/auth/login")
				     .contentType(APPLICATION_JSON)
				     .content(objectMapper.writeValueAsString(loginRequest)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$['Access Token']").exists());

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
}
