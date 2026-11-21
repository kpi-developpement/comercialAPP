package ma.gestcom.modules.document.dto;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Create / update payload of any commercial document (draft only).
 *
 * @param dateValidite         devis only
 * @param retourMarchandise    avoirs only
 * @param referenceFournisseur purchases only (supplier's own document number)
 */
public record DocumentRequest(
        @NotNull(message = "Le tiers est obligatoire") Long tiersId,
        @NotNull LocalDate dateDocument,
        @NotNull(message = "Le dépôt est obligatoire") Long depotId,
        @Size(max = 2000) String notes,
        LocalDate dateEcheance,
        LocalDate dateValidite,
        boolean retourMarchandise,
        @Size(max = 50) String referenceFournisseur,
        @NotEmpty(message = "Ajoutez au moins une ligne") @Valid List<DocumentLigneRequest> lignes) {
}
