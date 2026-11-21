package ma.gestcom.modules.utilisateur.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import ma.gestcom.common.enums.Role;

public record UtilisateurUpdateRequest(
        @NotBlank @Size(max = 120) String nomComplet,
        @Email @Size(max = 120) String email,
        @NotNull Role role,
        boolean actif) {
}
