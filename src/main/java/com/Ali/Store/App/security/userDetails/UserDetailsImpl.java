package com.Ali.Store.App.security.userDetails;

import com.Ali.Store.App.entities.userAndProfileUser.ProfileUser;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Getter
@RequiredArgsConstructor
@Builder
public class UserDetailsImpl implements UserDetails {
    private final String username;
    private final String password;
    private final Collection<GrantedAuthority> authorities;
    private final Long id;
    private final ProfileUser profileUser;

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

}
