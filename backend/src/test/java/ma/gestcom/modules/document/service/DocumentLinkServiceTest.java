package ma.gestcom.modules.document.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import ma.gestcom.common.enums.StatutDocument;
import ma.gestcom.common.enums.TypeDocument;
import ma.gestcom.common.exception.BusinessException;
import ma.gestcom.common.exception.ErrorCodes;
import ma.gestcom.modules.vente.entity.DocumentVente;
import ma.gestcom.modules.vente.entity.DocumentVenteLigne;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Partial transformation: remaining quantities and source status.
 */
class DocumentLinkServiceTest {

    private final DocumentLinkService service = new DocumentLinkService();
    private final Map<Long, DocumentVenteLigne> lignes = new HashMap<>();
    private final Function<Long, DocumentVenteLigne> chargeur = lignes::get;
    private DocumentVente bc;
    private DocumentVenteLigne ligneBc;

    @BeforeEach
    void setUp() {
        bc = new DocumentVente();
        bc.setId(1L);
        bc.setType(TypeDocument.BON_COMMANDE);
        bc.setNumero("BC-2026-00001");
        bc.setStatut(StatutDocument.VALIDE);
        bc.setTiersId(7L);
        ligneBc = DocumentCalculatorTest.ligne("10", "100", "0", "20");
        ligneBc.setId(11L);
        ligneBc.setQuantiteRestante(new BigDecimal("10"));
        bc.addLigne(ligneBc);
        lignes.put(11L, ligneBc);
    }

    @Test
    void livraisonPartiellePuisSolde() {
        DocumentVente bl1 = livraison(6);
        service.consommer(bl1, chargeur);
        assertThat(ligneBc.getQuantiteRestante()).isEqualByComparingTo("4");
        assertThat(bc.getStatut()).isEqualTo(StatutDocument.TRANSFORME_PARTIEL);
        assertThat(bl1.getLignes().get(0).getSourceDocumentId()).isEqualTo(1L);
        assertThat(bl1.getSources()).isEqualTo("BC-2026-00001");

        DocumentVente bl2 = livraison(4);
        service.consommer(bl2, chargeur);
        assertThat(ligneBc.getQuantiteRestante()).isEqualByComparingTo("0");
        assertThat(bc.getStatut()).isEqualTo(StatutDocument.TRANSFORME);
    }

    @Test
    void refuseDeDepasserLeReste() {
        service.consommer(livraison(6), chargeur);
        assertThatThrownBy(() -> service.consommer(livraison(5), chargeur))
                .isInstanceOf(BusinessException.class)
                .extracting("code").isEqualTo(ErrorCodes.QUANTITY_EXCEEDS_REMAINING);
        assertThat(ligneBc.getQuantiteRestante()).isEqualByComparingTo("4");
    }

    @Test
    void supprimerLeBrouillonRestitueLesQuantites() {
        DocumentVente bl = livraison(6);
        service.consommer(bl, chargeur);
        service.liberer(bl, chargeur);
        assertThat(ligneBc.getQuantiteRestante()).isEqualByComparingTo("10");
        assertThat(bc.getStatut()).isEqualTo(StatutDocument.VALIDE);
    }

    @Test
    void refuseUneTransformationNonAutorisee() {
        DocumentVente devis = new DocumentVente();
        devis.setType(TypeDocument.DEVIS);
        devis.setTiersId(7L);
        DocumentVenteLigne l = DocumentCalculatorTest.ligne("1", "100", "0", "20");
        l.setSourceLigneId(11L);
        devis.addLigne(l);
        assertThatThrownBy(() -> service.consommer(devis, chargeur))
                .isInstanceOf(BusinessException.class)
                .extracting("code").isEqualTo(ErrorCodes.TRANSFORMATION_NOT_ALLOWED);
    }

    @Test
    void refuseUnAutreTiers() {
        DocumentVente bl = livraison(1);
        bl.setTiersId(99L);
        assertThatThrownBy(() -> service.consommer(bl, chargeur))
                .isInstanceOf(BusinessException.class)
                .extracting("code").isEqualTo(ErrorCodes.GROUPING_INVALID);
    }

    private DocumentVente livraison(int quantite) {
        DocumentVente bl = new DocumentVente();
        bl.setType(TypeDocument.BON_LIVRAISON);
        bl.setTiersId(7L);
        bl.addLigne(DocumentLigneFactory.copier(DocumentVenteLigne::new, ligneBc, BigDecimal.valueOf(quantite), 1));
        return bl;
    }
}
