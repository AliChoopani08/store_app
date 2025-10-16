package com.Ali.Store.App.security.customizationAuthentication;

import com.Ali.Store.App.exceptions.security.DeviceNotAllowedException;
import com.Ali.Store.App.repository.userAndProfileUser.RepositoryRefreshToken;
import com.Ali.Store.App.security.userDetails.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final RepositoryRefreshToken repositoryRefreshToken;


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String rowPassword = (String)authentication.getCredentials();
        String deviceId = ((CustomAuthenticationToken)authentication).getDeviceId();

        final UserDetailsImpl user = (UserDetailsImpl) userDetailsService.loadUserByUsername(username);

        if (!passwordEncoder.matches(rowPassword, user.getPassword())) {
            throw new BadCredentialsException("Password is invalid !");
        }
        if (repositoryRefreshToken.findByUserIdAndDeviceId(user.getId(), deviceId).isEmpty()) {
            throw new DeviceNotAllowedException("This device is not allowed !");
        }

        return new CustomAuthenticationToken(user, null, user.getAuthorities(), deviceId);

    }

    @Override
    public boolean supports(Class<?> authentication) {
        return CustomAuthenticationToken.class
                .isAssignableFrom(authentication);
    }
}
