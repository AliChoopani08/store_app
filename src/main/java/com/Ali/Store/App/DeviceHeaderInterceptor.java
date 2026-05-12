package com.Ali.Store.App;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Component
public class DeviceHeaderInterceptor implements HandlerInterceptor {

    @Override
   public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        final String deviceUuid = request.getHeader("X-Device-UUID");
        if (deviceUuid == null || deviceUuid.isBlank()) {
            response.sendError(BAD_REQUEST.value(), "X-Device-UUID header is required !");

            return false;
        }

        return true;
    }
}
