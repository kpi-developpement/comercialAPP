package ma.gestcom.modules.reglement.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import ma.gestcom.common.enums.TypeReglement;

/**
 * @param reference     chèque / effet number (required for those modes) or transfer reference
 * @param dateEcheance  due date of a chèque / effet
 * @param affectations  invoices settled by this payment (may be empty: advance payment)
 */
public record ReglementRequest(
        @NotNull Long tiersId,
        @NotNull LocalDate dateReglement,
        @NotNull TypeReglement mode,
        @NotNull @DecimalMin(value = "0", inclusive = false) @Digits(integer = 13, fraction = 2) BigDecimal montant,
        @Size(max = 50) String reference,
        @Size(max = 80) String banque,
        LocalDate dateEcheance,
        @Size(max = 2000) String notes,
        @Valid List<AffectationRequest> affectations) {
}
