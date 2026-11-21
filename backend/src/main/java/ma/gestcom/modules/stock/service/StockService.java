package ma.gestcom.modules.stock.service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;

import ma.gestcom.modules.stock.dto.StockOperation;

/**
 * Single entry point that changes stock quantities. Every call writes a {@code StockMouvement}.
 * Non-stocked articles (services) are silently ignored.
 */
public interface StockService {

    /** Increases the stock; recomputes the global PUMP when {@code op.recalculerPump()} is true. */
    void entree(StockOperation op);

    /**
     * Decreases the stock. Throws STOCK_INSUFFICIENT when the depot quantity is too low,
     * unless the company setting {@code autoriserStockNegatif} is enabled.
     */
    void sortie(StockOperation op);

    /** Signed inventory adjustment (AJUSTEMENT_INVENTAIRE), valued at the current PUMP. */
    void ajuster(StockOperation op, BigDecimal ecartSigne);

    /**
     * Checks in one pass that the depot can serve all the requested quantities (article id -> quantity).
     * No-op when negative stock is allowed.
     */
    void verifierDisponibilite(Long depotId, Map<Long, BigDecimal> besoins);

    BigDecimal getQuantite(Long articleId, Long depotId);

    Map<Long, BigDecimal> getQuantites(Long depotId, Collection<Long> articleIds);
}
