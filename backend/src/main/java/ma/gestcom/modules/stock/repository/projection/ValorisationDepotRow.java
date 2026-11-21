package ma.gestcom.modules.stock.repository.projection;

import java.math.BigDecimal;

public interface ValorisationDepotRow {

    Long getDepotId();

    String getDepotCode();

    String getDepotLibelle();

    Long getNbArticles();

    BigDecimal getQuantite();

    BigDecimal getValeur();
}
