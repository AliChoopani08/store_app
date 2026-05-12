package com.Ali.Store.App.checkOut.controller;

import com.Ali.Store.App.security.jwt.JwtAuthServiceInterface;
import com.Ali.Store.App.service.checkOut.cart.CartService;
import com.Ali.Store.App.service.checkOut.order.OrderService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.web.AuthenticationEntryPoint;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class ConfigTestCheckOutController {

    @Bean
    public OrderService mockServiceOrder() {
        return mock(OrderService.class);
    }

    @Bean
    public CartService mockServiceCart() {
        return mock(CartService.class);
    }

    @Bean
    public JwtAuthServiceInterface mockSecurityBeans() {
        return mock(JwtAuthServiceInterface.class);
    }

    @Bean
    public AuthenticationEntryPoint mockAuthEntryPoint() {
        return mock(AuthenticationEntryPoint.class);
    }
}
