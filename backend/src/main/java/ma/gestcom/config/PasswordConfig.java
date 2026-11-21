package ma.gestcom.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Password hashing, kept apart from SecurityConfig so that services needing the encoder do not depend on the
 * security filter chain (avoids a bean cycle SecurityConfig → JwtAuthFilter → UtilisateurService → encoder).
 */
@Configuration
public class PasswordConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
