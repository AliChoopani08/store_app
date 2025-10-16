package com.Ali.Store.App.user.controller;


import com.Ali.Store.App.security.jwt.JwtServiceInterface;
import com.Ali.Store.App.service.user.authentication.AuthenticationServiceInterface;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class ConfigTest {
    @Bean
    public AuthenticationServiceInterface serviceUsersInterface() {
        return mock(AuthenticationServiceInterface.class);
    }

    @Bean
    public JwtServiceInterface mockJwtService() {
        return mock(JwtServiceInterface.class);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(auth ->
                auth.requestMatchers("/auth/register"
                                , "/auth/login"
                                , "/auth/access/token"
                                , "/product"

                                , "/swagger-ui.html"
                                , "/swagger-ui/**"
                                , "/v3/**"
                        )
                        .permitAll()
                        .requestMatchers("/admin/**" ,
                                "/check-out/**",
                                "/auth",
                                "/profile/**")
                        .authenticated()
                        .anyRequest()
                        .permitAll());
        return httpSecurity.build();
    }
}
