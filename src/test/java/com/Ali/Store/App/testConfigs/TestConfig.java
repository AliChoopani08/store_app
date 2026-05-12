package com.Ali.Store.App.testConfigs;

import com.Ali.Store.App.DeviceHeaderInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@TestConfiguration
@EnableJpaAuditing
@RequiredArgsConstructor
public class TestConfig implements WebMvcConfigurer {

    private final DeviceHeaderInterceptor deviceHeaderInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(deviceHeaderInterceptor)
                .addPathPatterns("/auth/login",
                        "/auth/access/token",
                        "/auth/refresh-token");
    }
}
