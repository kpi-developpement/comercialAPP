package ma.gestcom.modules.document.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;

import ma.gestcom.modules.document.dto.TvaRecapResponse;
import ma.gestcom.modules.vente.entity.DocumentVente;
import ma.gestcom.modules.vente.entity.DocumentVenteLigne;
import org.junit.jupiter.api.Test;

class DocumentCalculatorTest {

    @Test
    void calculeUneLigneAvecRemiseEtTva() {
        DocumentVenteLigne l = ligne("3", "33.333", "10", "20");
        DocumentCalculator.calculerLigne(l);
        // brut = 99.999 -> 100.00 ; remise 10 % = 10.00 ; HT = 90.00 ; TVA 20 % = 18.00
        assertThat(l.getMontantRemise()).isEqualByComparingTo("10.00");
        assertThat(l.getMontantHt()).isEqualByComparingTo("90.00");
        assertThat(l.getMontantTva()).isEqualByComparingTo("18.00");
        assertThat(l.getMontantTtc()).isEqualByComparingTo("108.00");
    }

    @Test
    void arrondiHalfUpParLignePuisSomme() {
        DocumentVente doc = new DocumentVente();
        doc.addLigne(ligne("1", "0.155", "0", "7"));
        doc.addLigne(ligne("1", "0.155", "0", "7"));
        doc.addLigne(ligne("1", "0.155", "0", "7"));
        DocumentCalculator.calculerTotaux(doc);
        // brut 0.155 -> 0.16 (HALF_UP) each, HT 0.48 ; TVA 0.16 * 7 % = 0.0112 -> 0.01 each
        assertThat(doc.getTotalHt()).isEqualByComparingTo("0.48");
        assertThat(doc.getTotalTva()).isEqualByComparingTo("0.03");
        assertThat(doc.getTotalTtc()).isEqualByComparingTo("0.51");
    }

    @Test
    void totauxEtRecapTvaParTaux() {
        DocumentVente doc = new DocumentVente();
        doc.addLigne(ligne("2", "100", "0", "20"));
        doc.addLigne(ligne("1", "50", "0", "20"));
        doc.addLigne(ligne("4", "25", "5", "10"));
        doc.addLigne(ligne("1", "30", "0", "0"));
        DocumentCalculator.calculerTotaux(doc);

        assertThat(doc.getTotalHt()).isEqualByComparingTo("375.00");
        assertThat(doc.getTotalRemise()).isEqualByComparingTo("5.00");
        assertThat(doc.getTotalTva()).isEqualByComparingTo("59.50");
        assertThat(doc.getTotalTtc()).isEqualByComparingTo("434.50");

        List<TvaRecapResponse> recap = DocumentCalculator.recapTva(doc.getLignes());
        assertThat(recap).extracting(TvaRecapResponse::taux)
                .usingElementComparator(BigDecimal::compareTo)
                .containsExactly(new BigDecimal("20"), new BigDecimal("10"), BigDecimal.ZERO);
        assertThat(recap.get(0).baseHt()).isEqualByComparingTo("250.00");
        assertThat(recap.get(0).montantTva()).isEqualByComparingTo("50.00");
        assertThat(recap.get(1).baseHt()).isEqualByComparingTo("95.00");
        assertThat(recap.get(1).montantTva()).isEqualByComparingTo("9.50");
        assertThat(recap.get(2).montantTva()).isEqualByComparingTo("0.00");
    }

    static DocumentVenteLigne ligne(String qte, String pu, String remise, String tva) {
        DocumentVenteLigne l = new DocumentVenteLigne();
        l.setQuantite(new BigDecimal(qte));
        l.setPrixUnitaireHt(new BigDecimal(pu));
        l.setRemisePct(new BigDecimal(remise));
        l.setTauxTva(new BigDecimal(tva));
        l.setDesignation("Article");
        return l;
    }
}
