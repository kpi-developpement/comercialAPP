package ma.gestcom.security.dto;

import java.time.Instant;

import ma.gestcom.modules.utilisateur.dto.UtilisateurResponse;

public record LoginResponse(String token, Instant expiresAt, UtilisateurResponse user) {
}
