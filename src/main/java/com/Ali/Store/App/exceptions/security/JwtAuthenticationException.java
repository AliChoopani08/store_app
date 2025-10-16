package com.Ali.Store.App.exceptions.security;

import org.springframework.security.core.AuthenticationException;

public class JwtAuthenticationException extends AuthenticationException {
    public JwtAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}

// What is the Throwable cause : With it, we can see the main root cause of The thrown exception with its exclusive error message
