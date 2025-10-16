package com.Ali.Store.App.product.controller;

import com.Ali.Store.App.security.jwt.JwtServiceImpl;
import com.Ali.Store.App.service.product.ServiceProductInterface;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class ConfigClassTest {

    @Bean
    public ServiceProductInterface mockServiceProductInterface() {
        return mock(ServiceProductInterface.class);
    }

  @Bean
    public JwtServiceImpl mockJwtService() {
        return mock(JwtServiceImpl.class);
  }
}
