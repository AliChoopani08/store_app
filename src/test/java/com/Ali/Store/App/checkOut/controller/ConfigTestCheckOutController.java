package com.Ali.Store.App.checkOut.controller;

import com.Ali.Store.App.security.jwt.JwtServiceInterface;
import com.Ali.Store.App.service.checkOut.cart.ServiceCartInterface;
import com.Ali.Store.App.service.checkOut.order.ServiceOrderInterface;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.web.AuthenticationEntryPoint;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class ConfigTestCheckOutController {

    @Bean
    public ServiceOrderInterface mockServiceOrder() {
        return mock(ServiceOrderInterface.class);
    }

    @Bean
    public ServiceCartInterface mockServiceCart() {
        return mock(ServiceCartInterface.class);
    }

    @Bean
    public JwtServiceInterface mockSecurityBeans() {
        return mock(JwtServiceInterface.class);
    }

    @Bean
    public AuthenticationEntryPoint mockAuthEntryPoint() {
        return mock(AuthenticationEntryPoint.class);
    }
}
