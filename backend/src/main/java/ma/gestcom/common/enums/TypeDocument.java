package ma.gestcom.common.enums;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

/**
 * Every numbered document type of the application. Also used as source reference of stock movements.
 */
public enum TypeDocument {
    DEVIS("devis", "Devis"),
    BON_COMMANDE("commandes", "Bon de commande"),
    BON_LIVRAISON("livraisons", "Bon de livraison"),
    FACTURE("factures", "Facture"),
    AVOIR("avoirs", "Avoir"),
    BC_FOURNISSEUR("commandes", "Bon de commande fournisseur"),
    RECEPTION("receptions", "Bon de réception"),
    FACTURE_FOURNISSEUR("factures", "Facture fournisseur"),
    AVOIR_FOURNISSEUR("avoirs", "Avoir fournisseur"),
    REGLEMENT_CLIENT("reglements-clients", "Règlement client"),
    REGLEMENT_FOURNISSEUR("reglements-fournisseurs", "Règlement fournisseur"),
    INVENTAIRE("inventaires", "Inventaire"),
    TRANSFERT("transferts", "Transfert");

    private static final Set<TypeDocument> VENTES = EnumSet.of(DEVIS, BON_COMMANDE, BON_LIVRAISON, FACTURE, AVOIR);
    private static final Set<TypeDocument> ACHATS =
            EnumSet.of(BC_FOURNISSEUR, RECEPTION, FACTURE_FOURNISSEUR, AVOIR_FOURNISSEUR);

    private final String path;
    private final String libelle;

    TypeDocument(String path, String libelle) {
        this.path = path;
        this.libelle = libelle;
    }

    public String getPath() {
        return path;
    }

    public String getLibelle() {
        return libelle;
    }

    public boolean isVente() {
        return VENTES.contains(this);
    }

    public boolean isAchat() {
        return ACHATS.contains(this);
    }

    public static TypeDocument venteFromPath(String path) {
        return fromPath(path, VENTES);
    }

    public static TypeDocument achatFromPath(String path) {
        return fromPath(path, ACHATS);
    }

    private static TypeDocument fromPath(String path, Set<TypeDocument> candidates) {
        return Arrays.stream(values())
                .filter(candidates::contains)
                .filter(t -> t.path.equals(path))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Type de document inconnu : " + path));
    }
}
