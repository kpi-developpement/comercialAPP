package ma.gestcom.modules.stock.dto;

import java.math.BigDecimal;

public record EtatStockResponse(
        Long articleId,
        String code,
        String designation,
        String familleLibelle,
        String unite,
        BigDecimal stockMinimum,
        BigDecimal quantite,
        BigDecimal pump,
        BigDecimal valeur,
        boolean sousMinimum) {
}
