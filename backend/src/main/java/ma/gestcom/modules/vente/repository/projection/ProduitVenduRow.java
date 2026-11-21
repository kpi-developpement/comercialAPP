package ma.gestcom.modules.vente.repository.projection;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface ProduitVenduRow {

    Long getArticleId();

    String getCode();

    String getDesignation();

    BigDecimal getQuantite();

    BigDecimal getMontantHt();

    Long getNbFactures();

    LocalDate getDerniereVente();
}
