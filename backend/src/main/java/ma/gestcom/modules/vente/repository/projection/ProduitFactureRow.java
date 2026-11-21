package ma.gestcom.modules.vente.repository.projection;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface ProduitFactureRow {

    Long getDocumentId();

    String getType();

    String getNumero();

    LocalDate getDateDocument();

    BigDecimal getPrixUnitaireHt();

    BigDecimal getQuantite();

    BigDecimal getMontantHt();
}
