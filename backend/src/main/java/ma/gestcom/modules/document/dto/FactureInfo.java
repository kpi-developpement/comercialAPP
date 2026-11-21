package ma.gestcom.modules.document.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import ma.gestcom.common.enums.TypeDocument;

/**
 * Invoice view used by the payment module to allocate payments.
 */
public record FactureInfo(
        Long id,
        TypeDocument type,
        String numero,
        Long tiersId,
        boolean valide,
        LocalDate dateEcheance,
        BigDecimal totalTtc,
        BigDecimal montantRegle,
        BigDecimal resteAPayer) {
}
