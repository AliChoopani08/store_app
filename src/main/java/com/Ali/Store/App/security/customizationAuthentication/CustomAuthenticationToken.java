package com.Ali.Store.App.security.customizationAuthentication;

import lombok.Getter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.UUID;

@Getter
public class CustomAuthenticationToken extends UsernamePasswordAuthenticationToken {

    private final UUID deviceUuid;

    /**
     * For Unauthenticated Stage
     */
    public CustomAuthenticationToken(Object principal) {
        super(principal, null);
        this.deviceUuid = null;
    }

    /**
     * For Login Stage
     */
    public CustomAuthenticationToken(Object principal, Object credentials, UUID deviceUuid) {
        super(principal, credentials);
        this.deviceUuid = deviceUuid;
    }

    /**
     * For Authentication Stage
     */
    public CustomAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities, UUID deviceUuid) {
        super(principal, credentials, authorities);
        this.deviceUuid = deviceUuid;
    }
}
