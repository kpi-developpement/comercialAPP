package ma.gestcom.modules.document.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import ma.gestcom.common.enums.TypeDocument;

/**
 * Transforms a validated document into the next one of the chain.
 *
 * @param cible             target document type (must be an allowed next step)
 * @param lignes            quantities to take per source line; empty or null = every remaining quantity
 * @param retourMarchandise only when the target is an avoir
 */
public record TransformationRequest(
        @NotNull TypeDocument cible,
        LocalDate dateDocument,
        boolean retourMarchandise,
        @Valid List<Ligne> lignes) {

    public record Ligne(@NotNull Long ligneId, @NotNull @DecimalMin("0") BigDecimal quantite) {
    }
}
