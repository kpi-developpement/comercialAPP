package ma.gestcom.modules.document.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import ma.gestcom.common.enums.StatutPaiement;
import ma.gestcom.common.enums.TypeDocument;
import org.junit.jupiter.api.Test;

/**
 * Stock is moved exactly once along the chain.
 */
class DocumentWorkflowTest {

    @Test
    void livraisonSortDuStock() {
        assertThat(DocumentWorkflow.effetStock(TypeDocument.BON_LIVRAISON, TypeDocument.BON_COMMANDE, false))
                .isEqualTo(StockEffet.SORTIE);
    }

    @Test
    void factureIssueDUnBlNeSortPasUneSecondeFois() {
        assertThat(DocumentWorkflow.effetStock(TypeDocument.FACTURE, TypeDocument.BON_LIVRAISON, false))
                .isEqualTo(StockEffet.AUCUN);
    }

    @Test
    void factureDirecteOuIssueDUnDevisSortDuStock() {
        assertThat(DocumentWorkflow.effetStock(TypeDocument.FACTURE, null, false)).isEqualTo(StockEffet.SORTIE);
        assertThat(DocumentWorkflow.effetStock(TypeDocument.FACTURE, TypeDocument.DEVIS, false))
                .isEqualTo(StockEffet.SORTIE);
        assertThat(DocumentWorkflow.effetStock(TypeDocument.FACTURE, TypeDocument.BON_COMMANDE, false))
                .isEqualTo(StockEffet.SORTIE);
    }

    @Test
    void avoirAvecRetourRentreEnStock() {
        assertThat(DocumentWorkflow.effetStock(TypeDocument.AVOIR, TypeDocument.FACTURE, true))
                .isEqualTo(StockEffet.ENTREE);
        assertThat(DocumentWorkflow.effetStock(TypeDocument.AVOIR, TypeDocument.FACTURE, false))
                .isEqualTo(StockEffet.AUCUN);
    }

    @Test
    void achats() {
        assertThat(DocumentWorkflow.effetStock(TypeDocument.RECEPTION, null, false)).isEqualTo(StockEffet.ENTREE);
        assertThat(DocumentWorkflow.effetStock(TypeDocument.FACTURE_FOURNISSEUR, TypeDocument.RECEPTION, false))
                .isEqualTo(StockEffet.AUCUN);
        assertThat(DocumentWorkflow.effetStock(TypeDocument.AVOIR_FOURNISSEUR, null, true))
                .isEqualTo(StockEffet.SORTIE);
        assertThat(DocumentWorkflow.effetStock(TypeDocument.DEVIS, null, false)).isEqualTo(StockEffet.AUCUN);
    }

    @Test
    void statutPaiement() {
        BigDecimal ttc = new BigDecimal("1200.00");
        assertThat(DocumentWorkflow.statutPaiement(BigDecimal.ZERO, ttc)).isEqualTo(StatutPaiement.NON_PAYEE);
        assertThat(DocumentWorkflow.statutPaiement(new BigDecimal("500"), ttc)).isEqualTo(StatutPaiement.PARTIELLE);
        assertThat(DocumentWorkflow.statutPaiement(ttc, ttc)).isEqualTo(StatutPaiement.PAYEE);
    }
}
