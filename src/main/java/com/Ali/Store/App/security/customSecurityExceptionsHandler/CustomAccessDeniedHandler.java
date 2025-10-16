package com.Ali.Store.App.security.customSecurityExceptionsHandler;

import com.Ali.Store.App.ResponseError;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import static jakarta.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static java.time.LocalDateTime.now;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final Logger logger = LoggerFactory.getLogger(CustomAccessDeniedHandler.class);

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {

        response.setContentType("application/json");
        response.setStatus(SC_FORBIDDEN);

        logger.error("Forbidden", accessDeniedException.getMessage());
        final ResponseError responseError = new ResponseError
                (now(), 403, "Forbidden", "You don't have permission to access this resource !", request.getRequestURI());

        final ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(responseError));
    }
}
