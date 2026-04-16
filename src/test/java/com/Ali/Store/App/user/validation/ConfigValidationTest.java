package com.Ali.Store.App.user.validation;

import com.Ali.Store.App.security.jwt.JwtAuthServiceInterface;
import com.Ali.Store.App.service.user.authentication.AuthenticationService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.web.AuthenticationEntryPoint;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class ConfigValidationTest {

    @Bean
    public JwtAuthServiceInterface mockJwtService() {
        return mock(JwtAuthServiceInterface.class);
    }

    @Bean
    public AuthenticationService mockAuthService( ){
        return mock(AuthenticationService.class);
    }

    @Bean
    public AuthenticationEntryPoint mockEntryPoint() {
        return mock(AuthenticationEntryPoint.class);
    }

}
