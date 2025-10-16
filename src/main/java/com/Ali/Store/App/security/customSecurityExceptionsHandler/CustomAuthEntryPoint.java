package com.Ali.Store.App.security.customSecurityExceptionsHandler;
import com.Ali.Store.App.ResponseError;
import com.Ali.Store.App.exceptions.security.DeviceNotAllowedException;
import com.Ali.Store.App.exceptions.security.JwtAuthenticationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import java.io.IOException;

import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static java.time.LocalDateTime.now;

@Component
@RequiredArgsConstructor
public class CustomAuthEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;
    private final Logger logger = LoggerFactory.getLogger(CustomAuthEntryPoint.class);

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        final String message = getAppropriateResponseMessage(authException);
        response.setStatus(SC_UNAUTHORIZED);

        logger.error("Authentication failed: {}", message);
        response.setContentType("application/json");

        final ResponseError responseError = new ResponseError(now(), 401, "UNAUTHORIZED", message, request.getRequestURI());

        response.getWriter().write(objectMapper.writeValueAsString(responseError));
    }
    private static String getAppropriateResponseMessage(AuthenticationException authException) {
        String message = "";
        if (authException instanceof BadCredentialsException) {
            message = "Username or password are invalid !";
        }
        else if (authException instanceof UsernameNotFoundException) {
            message = "This username doesn't exist in database !";
        }
        else if (authException instanceof LockedException) {
            message = "This account locked has been locked !";
        }
        else if (authException instanceof InsufficientAuthenticationException) {
            message = "To access this recourse, You must be logged in";
        }
        else if (authException instanceof JwtAuthenticationException ex){
            message = ex.getMessage();
        } else if (authException instanceof DeviceNotAllowedException ex) {
            message = ex.getMessage();
        }
        return message;
    }
}
