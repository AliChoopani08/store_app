package com.Ali.Store.App;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableScheduling
@EnableJpaAuditing // Automatic activation based on the user's creation and modification date
@Profile("!test")
@RequiredArgsConstructor
public class AppConfig implements WebMvcConfigurer {

    private final DeviceHeaderInterceptor deviceHeaderInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(deviceHeaderInterceptor)
                .addPathPatterns("/auth/login",
                        "/auth/access/token",
                        "/auth/refresh-token");
    }
}
