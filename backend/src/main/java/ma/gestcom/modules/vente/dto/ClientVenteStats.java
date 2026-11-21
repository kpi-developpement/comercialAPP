package ma.gestcom.modules.vente.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Responses of the "Produits vendus par client" screen. */
public final class ClientVenteStats {

    private ClientVenteStats() {
    }

    public record ProduitVendu(Long articleId, String code, String designation, BigDecimal quantite,
                               BigDecimal montantHt, long nbFactures, LocalDate derniereVente) {
    }

    /** One invoice (or credit note, negative) line of a product for the client. */
    public record ProduitFacture(Long documentId, String type, String numero, LocalDate dateDocument,
                                 BigDecimal prixUnitaireHt, BigDecimal quantite, BigDecimal montantHt) {
    }

    public record CaAnnuel(int annee, BigDecimal montantTtc) {
    }
}
