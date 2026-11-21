package ma.gestcom.modules.stock.dto;

import java.math.BigDecimal;
import java.time.Instant;

import ma.gestcom.common.enums.TypeDocument;
import ma.gestcom.common.enums.TypeMouvement;

public record MouvementResponse(
        Long id,
        Instant dateMouvement,
        Long articleId,
        String articleCode,
        String articleDesignation,
        Long depotId,
        String depotLibelle,
        TypeMouvement type,
        BigDecimal quantite,
        BigDecimal coutUnitaire,
        BigDecimal stockApres,
        BigDecimal pumpApres,
        TypeDocument referenceType,
        Long referenceId,
        String referenceNumero,
        String libelle,
        String createdBy) {
}
