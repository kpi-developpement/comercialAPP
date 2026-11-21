package ma.gestcom.common.enums;

import java.util.EnumSet;
import java.util.Set;

/**
 * Lifecycle of a payment instrument. Cash, transfer and card payments are created directly as ENCAISSE.
 */
public enum StatutReglement {
    EN_PORTEFEUILLE,
    REMIS_EN_BANQUE,
    ENCAISSE,
    IMPAYE;

    public Set<StatutReglement> transitionsAutorisees() {
        return switch (this) {
            case EN_PORTEFEUILLE -> EnumSet.of(REMIS_EN_BANQUE, ENCAISSE, IMPAYE);
            case REMIS_EN_BANQUE -> EnumSet.of(ENCAISSE, IMPAYE);
            case ENCAISSE, IMPAYE -> EnumSet.noneOf(StatutReglement.class);
        };
    }
}
