package com.Ali.Store.App.testConfigs;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@TestConfiguration
@EnableWebSecurity
public class TestSecurityConfig {

    @Bean
    public SecurityFilterChain setTestSecurityConfig(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/admin/**" ,
                                        "/check-out/**",
                                        "/auth/me/username",
                                        "/auth/me/password-verify",
                                        "/auth/me/password-reset",
                                        "/auth/refresh-token",
                                        "/profile/**").authenticated()
                        .requestMatchers("/auth/register"
                                        , "/auth/login"
                                        , "/auth/access/token"
                                        , "/product").permitAll()
                                .anyRequest()
                                .permitAll());
        return httpSecurity.build();
    }
}
