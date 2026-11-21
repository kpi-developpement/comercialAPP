package ma.gestcom.modules.stock.dto;

import java.time.LocalDate;

import ma.gestcom.common.enums.StatutOperation;

public record InventaireSummaryResponse(
        Long id,
        String numero,
        Long depotId,
        String depotLibelle,
        LocalDate dateInventaire,
        StatutOperation statut,
        int nbLignes,
        String createdBy) {
}
