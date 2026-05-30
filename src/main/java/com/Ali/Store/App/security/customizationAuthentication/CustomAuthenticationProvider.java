package com.Ali.Store.App.security.customizationAuthentication;

import com.Ali.Store.App.exceptions.security.DeviceNotAllowedException;
import com.Ali.Store.App.repository.RefreshTokenRepository;
import com.Ali.Store.App.repository.userAndProfileUser.UserRepository;
import com.Ali.Store.App.security.userDetails.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static java.util.Optional.empty;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String rowPassword = (String) authentication.getCredentials();
        UUID deviceUuid = ((CustomAuthenticationToken) authentication).getDeviceUuid();

        final UserDetailsImpl user = (UserDetailsImpl) userDetailsService.loadUserByUsername(username);

        if (!passwordEncoder.matches(rowPassword, user.getPassword())) {
            throw new BadCredentialsException("The entered password with registered password aren't match !");
        }
        if (userRepository.findByUsernameAndDeviceUuidAndIsAvailable(username, deviceUuid).isEmpty()) {
            throw new DeviceNotAllowedException(deviceUuid.toString());
        }

        return new CustomAuthenticationToken(user, empty(), user.getAuthorities(), deviceUuid);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return CustomAuthenticationToken.class
                .isAssignableFrom(authentication);
    }
}
