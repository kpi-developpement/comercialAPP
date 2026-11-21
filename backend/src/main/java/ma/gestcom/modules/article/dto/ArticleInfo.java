package ma.gestcom.modules.article.dto;

import java.math.BigDecimal;

import ma.gestcom.common.enums.ArticleType;

/**
 * Read-only article snapshot exposed to the other modules (documents, stock).
 */
public record ArticleInfo(
        Long id,
        String code,
        String designation,
        String unite,
        ArticleType type,
        BigDecimal prixAchatHt,
        BigDecimal prixVenteHt,
        BigDecimal tauxTva,
        BigDecimal pump,
        BigDecimal stockMinimum,
        boolean actif) {

    public boolean isStocke() {
        return type == ArticleType.STOCKE;
    }
}
