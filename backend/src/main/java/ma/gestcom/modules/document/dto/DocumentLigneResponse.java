package ma.gestcom.modules.document.dto;

import java.math.BigDecimal;

public record DocumentLigneResponse(
        Long id,
        Integer ordre,
        Long articleId,
        String articleCode,
        String designation,
        String unite,
        BigDecimal quantite,
        BigDecimal prixUnitaireHt,
        BigDecimal remisePct,
        BigDecimal tauxTva,
        BigDecimal montantRemise,
        BigDecimal montantHt,
        BigDecimal montantTva,
        BigDecimal montantTtc,
        BigDecimal quantiteRestante,
        Long sourceLigneId,
        Long sourceDocumentId,
        boolean articleStocke) {
}
