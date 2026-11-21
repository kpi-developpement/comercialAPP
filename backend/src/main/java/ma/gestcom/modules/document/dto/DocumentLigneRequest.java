package ma.gestcom.modules.document.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * A document line as edited in the lines grid.
 *
 * @param articleId     null for a free text line (no stock impact)
 * @param designation   defaults to the article designation
 * @param prixUnitaireHt defaults to the article sale (or purchase) price
 * @param tauxTva       defaults to the article VAT rate
 * @param sourceLigneId set when the line comes from a transformation; kept as-is when a transformed draft is edited
 */
public record DocumentLigneRequest(
        Long articleId,
        @Size(max = 255) String designation,
        @NotNull @DecimalMin(value = "0", inclusive = false, message = "La quantité doit être positive")
        @Digits(integer = 12, fraction = 3) BigDecimal quantite,
        @DecimalMin("0") @Digits(integer = 12, fraction = 3) BigDecimal prixUnitaireHt,
        @DecimalMin("0") @DecimalMax("100") BigDecimal remisePct,
        @DecimalMin("0") @DecimalMax("100") BigDecimal tauxTva,
        Long sourceLigneId) {
}
