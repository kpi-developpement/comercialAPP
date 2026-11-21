package ma.gestcom.modules.stock.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import ma.gestcom.common.util.MoneyUtil;

/**
 * Prix Unitaire Moyen Pondéré:
 * {@code nouveauPump = (qteStock * ancienPump + qteEntree * cout) / (qteStock + qteEntree)}.
 * When the previous total stock is zero or negative, the new PUMP is the entry cost.
 */
public final class PumpCalculator {

    private PumpCalculator() {
    }

    public static BigDecimal calculer(BigDecimal qteStockTotal, BigDecimal ancienPump,
                                      BigDecimal qteEntree, BigDecimal coutUnitaire) {
        BigDecimal stock = MoneyUtil.nz(qteStockTotal);
        BigDecimal entree = MoneyUtil.nz(qteEntree);
        BigDecimal cout = MoneyUtil.nz(coutUnitaire);
        if (stock.signum() <= 0) {
            return MoneyUtil.qty(cout);
        }
        BigDecimal denominateur = stock.add(entree);
        if (denominateur.signum() <= 0) {
            return MoneyUtil.qty(cout);
        }
        BigDecimal valeur = stock.multiply(MoneyUtil.nz(ancienPump)).add(entree.multiply(cout));
        return valeur.divide(denominateur, MoneyUtil.QTY_SCALE, RoundingMode.HALF_UP);
    }
}
