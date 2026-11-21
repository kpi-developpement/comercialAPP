package ma.gestcom.modules.utilisateur.dto;

import ma.gestcom.common.enums.Role;

/**
 * Minimal credentials view consumed by the security layer only.
 */
public record AuthUser(String username, String passwordHash, Role role, boolean actif) {
}
