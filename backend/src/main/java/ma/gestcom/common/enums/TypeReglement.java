package ma.gestcom.common.enums;

/**
 * Payment instruments. Chèque and effet (LCN) carry a number, a bank and a due date and follow a status lifecycle.
 */
public enum TypeReglement {
    ESPECES,
    CHEQUE,
    EFFET,
    VIREMENT,
    CARTE;

    public boolean isTitre() {
        return this == CHEQUE || this == EFFET;
    }
}
