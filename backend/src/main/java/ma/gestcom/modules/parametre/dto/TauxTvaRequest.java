package ma.gestcom.modules.parametre.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TauxTvaRequest(
        @NotNull @DecimalMin("0") @DecimalMax("100") BigDecimal taux,
        @NotBlank @Size(max = 50) String libelle,
        boolean actif,
        boolean parDefaut) {
}
