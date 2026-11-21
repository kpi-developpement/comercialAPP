package ma.gestcom.modules.stock.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import ma.gestcom.common.enums.StatutOperation;

public record InventaireResponse(
        Long id,
        String numero,
        Long depotId,
        String depotLibelle,
        LocalDate dateInventaire,
        StatutOperation statut,
        String notes,
        List<Ligne> lignes) {

    public record Ligne(
            Long id,
            Long articleId,
            String articleCode,
            String designation,
            BigDecimal quantiteTheorique,
            BigDecimal quantiteComptee,
            BigDecimal ecart) {
    }
}
