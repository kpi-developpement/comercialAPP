package ma.gestcom.modules.document.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import ma.gestcom.common.enums.StatutDocument;
import ma.gestcom.common.enums.StatutPaiement;
import ma.gestcom.common.enums.TypeDocument;

/**
 * Full document (header + lines + VAT summary) for sales and purchases.
 *
 * @param transformationsPossibles document types this document can currently be transformed into
 * @param warnings                 non-blocking alerts (e.g. client credit limit exceeded)
 */
public record DocumentResponse(
        Long id,
        TypeDocument type,
        String numero,
        LocalDate dateDocument,
        StatutDocument statut,
        Long tiersId,
        String tiersCode,
        String tiersNom,
        String tiersIce,
        String tiersAdresse,
        Long depotId,
        String depotLibelle,
        String notes,
        BigDecimal totalHt,
        BigDecimal totalRemise,
        BigDecimal totalTva,
        BigDecimal totalTtc,
        LocalDate dateEcheance,
        LocalDate dateValidite,
        BigDecimal montantRegle,
        BigDecimal resteAPayer,
        StatutPaiement statutPaiement,
        boolean retourMarchandise,
        String sources,
        String referenceFournisseur,
        String montantEnLettres,
        List<DocumentLigneResponse> lignes,
        List<TvaRecapResponse> recapTva,
        List<TypeDocument> transformationsPossibles,
        List<String> warnings,
        String createdBy,
        Instant createdAt,
        Instant updatedAt,
        Long version) {
}
