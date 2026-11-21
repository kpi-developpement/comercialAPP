package ma.gestcom.modules.reglement.dto;

import java.math.BigDecimal;
import java.util.List;

import ma.gestcom.modules.document.dto.DocumentSummaryResponse;

/**
 * Balance of a tiers: {@code solde = factures - avoirs - règlements affectés - règlements non affectés}.
 * A positive solde is what the client owes us (or what we owe the supplier).
 */
public record SoldeTiersResponse(
        Long tiersId,
        BigDecimal totalFactures,
        BigDecimal totalAvoirs,
        BigDecimal totalRegle,
        BigDecimal reglementsNonAffectes,
        BigDecimal solde,
        List<DocumentSummaryResponse> facturesNonSoldees) {
}
