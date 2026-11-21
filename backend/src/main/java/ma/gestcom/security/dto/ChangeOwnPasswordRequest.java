package ma.gestcom.security.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangeOwnPasswordRequest(
        @NotBlank String ancienMotDePasse,
        @NotBlank @Size(min = 8, message = "8 caractères minimum") String nouveauMotDePasse) {
}
