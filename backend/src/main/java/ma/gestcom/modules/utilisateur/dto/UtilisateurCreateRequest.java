package ma.gestcom.modules.utilisateur.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import ma.gestcom.common.enums.Role;

public record UtilisateurCreateRequest(
        @NotBlank @Size(min = 3, max = 50)
        @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "Lettres, chiffres, . _ - uniquement")
        String username,
        @NotBlank @Size(max = 120) String nomComplet,
        @Email @Size(max = 120) String email,
        @NotNull Role role,
        boolean actif,
        @NotBlank @Size(min = 8, message = "8 caractères minimum") String motDePasse) {
}
