package ma.gestcom.modules.utilisateur.dto;

import ma.gestcom.common.enums.Role;

public record UtilisateurFilter(String search, Role role, Boolean actif) {
}
