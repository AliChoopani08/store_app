package com.Ali.Store.App.security.userDetails;

import com.Ali.Store.App.entities.userAndProfileUser.Users;
import com.Ali.Store.App.repository.userAndProfileUser.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static java.util.Collections.singleton;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository repositoryUser;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final Users foundUser = repositoryUser.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("This username doesn't exist in database !"));

        return UserDetailsImpl.builder()
                .username(foundUser.getUsername())
                .password(foundUser.getPassword())
                .authorities(singleton(new SimpleGrantedAuthority(foundUser.getRole().name())))
                .id(foundUser.getId())
                .profileUser(foundUser.getProfile())
                .build();
    }
}
