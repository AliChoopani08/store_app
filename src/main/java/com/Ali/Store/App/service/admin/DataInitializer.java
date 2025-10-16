package com.Ali.Store.App.service.admin;

import com.Ali.Store.App.entities.userAndProfileUser.ProfileUser;
import com.Ali.Store.App.entities.userAndProfileUser.RefreshToken;
import com.Ali.Store.App.entities.userAndProfileUser.Users;
import com.Ali.Store.App.repository.userAndProfileUser.RepositoryUser;
import com.Ali.Store.App.security.jwt.JwtServiceInterface;
import com.Ali.Store.App.security.refreshToken.RefreshTokenServiceInterface;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import static com.Ali.Store.App.entities.userAndProfileUser.Role.ROLE_ADMIN;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner init(RepositoryUser repository, PasswordEncoder passwordEncoder, JwtServiceInterface jwtService, RefreshTokenServiceInterface refreshTokenService) {
        return _ -> {
            if (repository.count() == 0) {
                Users admin = new Users();
                ProfileUser profileAdmin = new ProfileUser();
                admin.setUsername("09123456789");
                admin.setPassword(passwordEncoder.encode("Ali12345"));
                admin.setRole(ROLE_ADMIN);
                admin.setProfileFields(profileAdmin);

                final Users savedUser = repository.save(admin);

                final RefreshToken refreshToken = refreshTokenService.createRefreshToken(savedUser, "acer315-55kg", "Windows-Acer-Computer");

                final String accessToken = jwtService.generateToken(savedUser.getUsername());

                System.out.println("This your access token Ali ( " + accessToken + " );");
                System.out.println("This your refresh token Ali ( " + refreshToken + " );");
            }
        };
    }
}
