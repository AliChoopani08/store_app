package com.Ali.Store.App.security.customizationAuthentication;

import lombok.Getter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
public class CustomAuthenticationToken extends UsernamePasswordAuthenticationToken {

    private final String deviceId;

    /**
     * For Unauthenticated Stage
     */
    public CustomAuthenticationToken(Object principal) {
        super(principal, null);
        this.deviceId = null;
    }

    /**
     * For Login Stage
     */
    public CustomAuthenticationToken(Object principal, Object credentials, String deviceId) {
        super(principal, credentials);
        this.deviceId = deviceId;
    }

    /**
     * For Authentication Stage
     */
    public CustomAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities, String deviceId) {
        super(principal, credentials, authorities);
        this.deviceId = deviceId;
    }
}
