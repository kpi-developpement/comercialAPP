package ma.gestcom.security;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import ma.gestcom.config.GestcomProperties;
import org.springframework.stereotype.Service;

/**
 * Issues and verifies HS256 signed JWT access tokens.
 */
@Service
public class JwtService {

    private static final String ROLE_CLAIM = "role";

    private final SecretKey key;
    private final long expirationMinutes;

    public JwtService(GestcomProperties properties) {
        byte[] secret = properties.jwt().secret().getBytes(StandardCharsets.UTF_8);
        if (secret.length < 32) {
            throw new IllegalStateException("gestcom.jwt.secret doit contenir au moins 32 caractères");
        }
        this.key = Keys.hmacShaKeyFor(secret);
        this.expirationMinutes = properties.jwt().expirationMinutes();
    }

    public IssuedToken generate(String username, String role) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(expirationMinutes, ChronoUnit.MINUTES);
        String token = Jwts.builder()
                .subject(username)
                .claim(ROLE_CLAIM, role)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiresAt))
                .signWith(key)
                .compact();
        return new IssuedToken(token, expiresAt);
    }

    /** Returns the username if the token is well-formed, correctly signed and not expired. */
    public Optional<String> validateAndGetUsername(String token) {
        try {
            Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
            return Optional.ofNullable(claims.getSubject());
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public record IssuedToken(String token, Instant expiresAt) {
    }
}
