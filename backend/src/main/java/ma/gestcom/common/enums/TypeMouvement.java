package ma.gestcom.common.enums;

public enum TypeMouvement {
    ENTREE_ACHAT(true),
    SORTIE_VENTE(false),
    RETOUR_CLIENT(true),
    RETOUR_FOURNISSEUR(false),
    TRANSFERT_ENTREE(true),
    TRANSFERT_SORTIE(false),
    AJUSTEMENT_INVENTAIRE(true);

    private final boolean entree;

    TypeMouvement(boolean entree) {
        this.entree = entree;
    }

    /** Natural direction of the movement (an inventory adjustment can be signed either way). */
    public boolean isEntree() {
        return entree;
    }
}
