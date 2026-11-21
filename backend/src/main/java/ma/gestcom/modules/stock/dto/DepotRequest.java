package ma.gestcom.modules.stock.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DepotRequest(
        @NotBlank @Size(max = 20) String code,
        @NotBlank @Size(max = 100) String libelle,
        @Size(max = 255) String adresse,
        boolean actif,
        boolean parDefaut) {
}
