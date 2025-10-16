package com.Ali.Store.App.security.customizationAuthentication;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class CustomAuthenticationToken extends UsernamePasswordAuthenticationToken {

    private final String deviceId;

    public CustomAuthenticationToken(Object principal, Object credentials, String deviceId) {
        super(principal, credentials);
        this.deviceId = deviceId;
    }

    public CustomAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities, String deviceId) {
        super(principal, credentials, authorities);
        this.deviceId = deviceId;
    }

    public String getDeviceId() {
        return deviceId;
    }
}
