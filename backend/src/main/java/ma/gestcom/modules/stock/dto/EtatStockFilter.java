package ma.gestcom.modules.stock.dto;

public record EtatStockFilter(String search, Long familleId, Long depotId, Boolean sousMinimum) {
}
