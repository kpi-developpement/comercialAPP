package ma.gestcom.modules.document.dto;

import java.math.BigDecimal;

public record TvaRecapResponse(BigDecimal taux, BigDecimal baseHt, BigDecimal montantTva) {
}
