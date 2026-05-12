package com.Ali.Store.App.exceptions.security;

import org.springframework.security.core.AuthenticationException;

public class DeviceNotAllowedException extends AuthenticationException {
    public DeviceNotAllowedException(String deviceId) {
        super("This device [" + deviceId + "] is not exist  or it is blocked !");
    }
}
