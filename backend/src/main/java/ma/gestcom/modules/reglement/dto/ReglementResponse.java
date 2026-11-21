package ma.gestcom.modules.reglement.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import ma.gestcom.common.enums.SensTiers;
import ma.gestcom.common.enums.StatutReglement;
import ma.gestcom.common.enums.TypeReglement;

public record ReglementResponse(
        Long id,
        String numero,
        SensTiers sens,
        Long tiersId,
        String tiersNom,
        LocalDate dateReglement,
        TypeReglement mode,
        BigDecimal montant,
        BigDecimal montantAffecte,
        BigDecimal montantNonAffecte,
        String reference,
        String banque,
        LocalDate dateEcheance,
        StatutReglement statut,
        String notes,
        List<Affectation> affectations,
        String createdBy,
        Instant createdAt) {

    public record Affectation(Long id, Long documentId, String documentNumero, BigDecimal montant) {
    }
}
