package ma.gestcom.modules.stock.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import ma.gestcom.common.enums.StatutOperation;

public record TransfertResponse(
        Long id,
        String numero,
        Long depotSourceId,
        String depotSourceLibelle,
        Long depotDestinationId,
        String depotDestinationLibelle,
        LocalDate dateTransfert,
        StatutOperation statut,
        String notes,
        List<Ligne> lignes,
        String createdBy) {

    public record Ligne(Long id, Long articleId, String articleCode, String designation, BigDecimal quantite) {
    }
}
