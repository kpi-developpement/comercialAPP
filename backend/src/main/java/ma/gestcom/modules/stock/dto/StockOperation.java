package ma.gestcom.modules.stock.dto;

import java.math.BigDecimal;

import ma.gestcom.common.enums.TypeDocument;
import ma.gestcom.common.enums.TypeMouvement;

/**
 * A stock movement request issued by another module.
 *
 * @param quantite        always positive; the direction comes from the called method (entree / sortie)
 * @param coutUnitaire    unit cost of an entry (used for the PUMP); ignored for exits (current PUMP is used)
 * @param recalculerPump  true only for purchase entries
 */
public record StockOperation(
        Long articleId,
        Long depotId,
        BigDecimal quantite,
        BigDecimal coutUnitaire,
        TypeMouvement type,
        boolean recalculerPump,
        TypeDocument referenceType,
        Long referenceId,
        String referenceNumero,
        String libelle) {
}
