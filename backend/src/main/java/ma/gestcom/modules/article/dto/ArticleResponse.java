package ma.gestcom.modules.article.dto;

import java.math.BigDecimal;
import java.time.Instant;

import ma.gestcom.common.enums.ArticleType;

public record ArticleResponse(
        Long id,
        String code,
        String designation,
        Long familleId,
        String familleLibelle,
        String unite,
        BigDecimal prixAchatHt,
        BigDecimal prixVenteHt,
        BigDecimal tauxTva,
        String codeBarres,
        BigDecimal stockMinimum,
        boolean actif,
        ArticleType type,
        BigDecimal pump,
        Instant createdAt,
        Instant updatedAt) {
}
