package com.Ali.Store.App.exceptions.security;

import org.springframework.security.core.AuthenticationException;

public class DeviceNotAllowedException extends AuthenticationException {
    public DeviceNotAllowedException(String message) {
        super(message);
    }
}
