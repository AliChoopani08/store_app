package com.Ali.Store.App.product.controller;

import com.Ali.Store.App.security.jwt.JwtAuthServiceInterface;
import com.Ali.Store.App.service.product.ProductService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.web.AuthenticationEntryPoint;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class ConfigClassTest {

    @Bean
    public ProductService mockServiceProductInterface() {
        return mock(ProductService.class);
    }

  @Bean
    public JwtAuthServiceInterface mockJwtService() {
        return mock(JwtAuthServiceInterface.class);
  }

    @Bean
    public AuthenticationEntryPoint mockAuthEntryPoint() {
        return mock(AuthenticationEntryPoint.class);
    }
}
