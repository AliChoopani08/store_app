package com.Ali.Store.App.user.validation;


import com.Ali.Store.App.controller.user.AuthenticationController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthenticationController.class)
@Import(ConfigValidationTest.class)
@AutoConfigureMockMvc()
public class ValidationCalssShould {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void return_notFound_when_URL_is_invalid() throws Exception {
        String jsonUser = """
                {
                 "username": "09287382890",
                "password": "Alijdhf546",
                "deviceId": "acer234-756"
                }
                """;

        mockMvc.perform(post("/userks")
                .contentType(APPLICATION_JSON)
                .content(jsonUser))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Invalid URL"));
    }

    @Test
    void return_notFound_when_URL_and_method_are_not_match() throws Exception {
        String jsonUser = """
                {
                "username": "09287382890",
                "password": "Alijdhf546",
                "deviceId": "acer234-756"
                }
                """;
        mockMvc.perform(get("/auth/register")
                .contentType(APPLICATION_JSON)
                .content(jsonUser))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Being Match URL And Method"));
    }
}
