package ma.gestcom.modules.vente.repository.projection;

import java.math.BigDecimal;

public interface CaAnnuelRow {

    Integer getAnnee();

    BigDecimal getMontant();
}
