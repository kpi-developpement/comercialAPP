package ma.gestcom.modules.document.service;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ma.gestcom.common.enums.StatutPaiement;
import ma.gestcom.common.enums.TypeDocument;
import ma.gestcom.modules.document.entity.AbstractDocument;

/**
 * Business rules of the document chains:
 * <ul>
 *   <li>Ventes: Devis → Bon de commande → Bon de livraison → Facture → Avoir (steps may be skipped)</li>
 *   <li>Achats: BC fournisseur → Réception → Facture fournisseur → Avoir fournisseur</li>
 * </ul>
 * and their stock effects (a quantity is never moved twice: invoices coming from a BL / réception do not move stock).
 */
public final class DocumentWorkflow {

    private static final Map<TypeDocument, Set<TypeDocument>> TRANSFORMATIONS = new EnumMap<>(TypeDocument.class);

    static {
        TRANSFORMATIONS.put(TypeDocument.DEVIS,
                EnumSet.of(TypeDocument.BON_COMMANDE, TypeDocument.BON_LIVRAISON, TypeDocument.FACTURE));
        TRANSFORMATIONS.put(TypeDocument.BON_COMMANDE, EnumSet.of(TypeDocument.BON_LIVRAISON, TypeDocument.FACTURE));
        TRANSFORMATIONS.put(TypeDocument.BON_LIVRAISON, EnumSet.of(TypeDocument.FACTURE));
        TRANSFORMATIONS.put(TypeDocument.FACTURE, EnumSet.of(TypeDocument.AVOIR));
        TRANSFORMATIONS.put(TypeDocument.BC_FOURNISSEUR,
                EnumSet.of(TypeDocument.RECEPTION, TypeDocument.FACTURE_FOURNISSEUR));
        TRANSFORMATIONS.put(TypeDocument.RECEPTION, EnumSet.of(TypeDocument.FACTURE_FOURNISSEUR));
        TRANSFORMATIONS.put(TypeDocument.FACTURE_FOURNISSEUR, EnumSet.of(TypeDocument.AVOIR_FOURNISSEUR));
    }

    private static final Set<TypeDocument> ANNULABLES =
            EnumSet.of(TypeDocument.DEVIS, TypeDocument.BON_COMMANDE, TypeDocument.BC_FOURNISSEUR);

    private DocumentWorkflow() {
    }

    public static boolean peutTransformer(TypeDocument source, TypeDocument cible) {
        return TRANSFORMATIONS.getOrDefault(source, Set.of()).contains(cible);
    }

    /** Targets available for a document right now (validated and with remaining quantities). */
    public static List<TypeDocument> ciblesPossibles(AbstractDocument<?> document) {
        boolean resteQuantite = document.getLignes().stream().anyMatch(l -> l.getQuantiteRestante().signum() > 0);
        if (!document.getStatut().isTransformable() || !resteQuantite) {
            return List.of();
        }
        return List.copyOf(TRANSFORMATIONS.getOrDefault(document.getType(), Set.of()));
    }

    /** Only commitment documents without any downstream document can be cancelled; others use an avoir. */
    public static boolean estAnnulable(TypeDocument type) {
        return ANNULABLES.contains(type);
    }

    public static boolean estFacture(TypeDocument type) {
        return type == TypeDocument.FACTURE || type == TypeDocument.FACTURE_FOURNISSEUR;
    }

    public static boolean estAvoir(TypeDocument type) {
        return type == TypeDocument.AVOIR || type == TypeDocument.AVOIR_FOURNISSEUR;
    }

    /** Payment status of an invoice from its settled amount. */
    public static StatutPaiement statutPaiement(BigDecimal regle, BigDecimal ttc) {
        if (regle.signum() == 0) {
            return StatutPaiement.NON_PAYEE;
        }
        return regle.compareTo(ttc) >= 0 ? StatutPaiement.PAYEE : StatutPaiement.PARTIELLE;
    }

    /**
     * @param type       type of the document being validated
     * @param sourceType type of the document the line was transformed from (null for a direct line)
     */
    public static StockEffet effetStock(TypeDocument type, TypeDocument sourceType, boolean retourMarchandise) {
        return switch (type) {
            case BON_LIVRAISON -> StockEffet.SORTIE;
            case FACTURE -> sourceType == TypeDocument.BON_LIVRAISON ? StockEffet.AUCUN : StockEffet.SORTIE;
            case AVOIR -> retourMarchandise ? StockEffet.ENTREE : StockEffet.AUCUN;
            case RECEPTION -> StockEffet.ENTREE;
            case FACTURE_FOURNISSEUR -> sourceType == TypeDocument.RECEPTION ? StockEffet.AUCUN : StockEffet.ENTREE;
            case AVOIR_FOURNISSEUR -> retourMarchandise ? StockEffet.SORTIE : StockEffet.AUCUN;
            default -> StockEffet.AUCUN;
        };
    }
}
