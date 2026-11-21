package ma.gestcom.modules.stock.dto;

import java.math.BigDecimal;

public record StockParDepotResponse(Long depotId, String depotCode, String depotLibelle, BigDecimal quantite) {
}
