package ma.gestcom.modules.article.dto;

import java.math.BigDecimal;

import ma.gestcom.common.enums.ArticleType;

public record ArticleSummaryResponse(
        Long id,
        String code,
        String designation,
        String familleLibelle,
        String unite,
        BigDecimal prixAchatHt,
        BigDecimal prixVenteHt,
        BigDecimal tauxTva,
        String codeBarres,
        ArticleType type,
        boolean actif,
        BigDecimal pump) {
}
