package ma.gestcom.modules.utilisateur.dto;

import java.time.Instant;

import ma.gestcom.common.enums.Role;

public record UtilisateurResponse(
        Long id,
        String username,
        String nomComplet,
        String email,
        Role role,
        boolean actif,
        Instant createdAt) {
}
