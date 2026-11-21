package ma.gestcom.modules.numerotation.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record NumerotationConfigRequest(
        @NotBlank @Pattern(regexp = "^[A-Z0-9]{1,10}$", message = "Majuscules et chiffres, 10 caractères max")
        String prefixe,
        @NotNull @Min(3) @Max(10) Integer longueur) {
}
