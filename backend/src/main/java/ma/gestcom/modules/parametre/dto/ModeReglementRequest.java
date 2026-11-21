package ma.gestcom.modules.parametre.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ModeReglementRequest(@NotBlank @Size(max = 50) String libelle, boolean actif) {
}
