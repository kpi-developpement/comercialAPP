package ma.gestcom.modules.stock.dto;

import java.math.BigDecimal;
import java.util.List;

public record ValorisationResponse(List<Depot> depots, BigDecimal quantiteTotale, BigDecimal valeurTotale) {

    public record Depot(Long depotId, String depotCode, String depotLibelle, long nbArticles,
                        BigDecimal quantite, BigDecimal valeur) {
    }
}
