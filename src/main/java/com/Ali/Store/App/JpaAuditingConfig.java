package com.Ali.Store.App;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing // Automatic activation based on the user's creation and modification date
@Profile("!test")
public class JpaAuditingConfig {
}
