package com.Ali.Store.App.security.userDetails;

import com.Ali.Store.App.entities.userAndProfileUser.Users;
import com.Ali.Store.App.repository.userAndProfileUser.RepositoryUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static java.util.Collections.singleton;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final RepositoryUser repositoryUser;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final Users foundUser = repositoryUser.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("This username doesn't exist in database !"));

        return new UserDetailsImpl(foundUser.getUsername(), foundUser.getPassword(),
                singleton(new SimpleGrantedAuthority(foundUser.getRole().name())), foundUser.getId(), foundUser.getProfile());
    }
}
