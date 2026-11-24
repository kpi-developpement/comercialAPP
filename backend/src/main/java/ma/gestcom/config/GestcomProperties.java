package ma.gestcom.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Application specific configuration, bound from the {@code gestcom.*} keys of application.yml.
 */
@ConfigurationProperties(prefix = "gestcom")
public record GestcomProperties(Jwt jwt, Cors cors, Admin admin, Accounts accounts) {

    public record Jwt(String secret, long expirationMinutes) {
    }

    public record Cors(List<String> allowedOrigins) {
    }

    /** Administrator account guaranteed at startup (created if no active ADMIN exists). */
    public record Admin(String username, String password) {
    }

    /** Default COMMERCIAL / MAGASINIER / COMPTABLE accounts guaranteed at startup (one per role). */
    public record Accounts(String password, String commercial, String magasinier, String comptable) {
    }
}
