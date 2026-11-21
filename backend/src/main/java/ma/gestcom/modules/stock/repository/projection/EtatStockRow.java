package ma.gestcom.modules.stock.repository.projection;

import java.math.BigDecimal;

/**
 * Native projection of the stock status report.
 */
public interface EtatStockRow {

    Long getArticleId();

    String getCode();

    String getDesignation();

    String getFamilleLibelle();

    String getUnite();

    BigDecimal getStockMinimum();

    BigDecimal getPump();

    BigDecimal getQuantite();

    BigDecimal getValeur();
}
