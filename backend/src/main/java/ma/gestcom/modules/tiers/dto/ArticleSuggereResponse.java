package ma.gestcom.modules.tiers.dto;

import java.math.BigDecimal;

/** Entry of a client's suggested products catalogue. */
public record ArticleSuggereResponse(Long id, Long articleId, String code, String designation, BigDecimal prixVenteHt) {
}
