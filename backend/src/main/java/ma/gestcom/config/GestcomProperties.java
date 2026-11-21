package ma.gestcom.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Application specific configuration, bound from the {@code gestcom.*} keys of application.yml.
 */
@ConfigurationProperties(prefix = "gestcom")
public record GestcomProperties(Jwt jwt, Cors cors, Admin admin) {

    public record Jwt(String secret, long expirationMinutes) {
    }

    public record Cors(List<String> allowedOrigins) {
    }

    /** Initial administrator created at startup when the user table is empty. */
    public record Admin(String username, String password) {
    }
}
