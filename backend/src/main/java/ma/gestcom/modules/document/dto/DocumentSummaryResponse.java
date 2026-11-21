package ma.gestcom.modules.document.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import ma.gestcom.common.enums.StatutDocument;
import ma.gestcom.common.enums.StatutPaiement;
import ma.gestcom.common.enums.TypeDocument;

public record DocumentSummaryResponse(
        Long id,
        TypeDocument type,
        String numero,
        LocalDate dateDocument,
        StatutDocument statut,
        Long tiersId,
        String tiersCode,
        String tiersNom,
        BigDecimal totalHt,
        BigDecimal totalTtc,
        LocalDate dateEcheance,
        BigDecimal montantRegle,
        BigDecimal resteAPayer,
        StatutPaiement statutPaiement,
        String sources,
        String createdBy) {
}
