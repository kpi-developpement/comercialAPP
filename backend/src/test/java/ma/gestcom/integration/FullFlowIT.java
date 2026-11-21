package ma.gestcom.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import ma.gestcom.common.enums.ArticleType;
import ma.gestcom.common.enums.RisqueClient;
import ma.gestcom.common.enums.TypeClient;
import ma.gestcom.common.enums.TypeFournisseur;
import ma.gestcom.common.enums.SensTiers;
import ma.gestcom.common.enums.StatutDocument;
import ma.gestcom.common.enums.StatutPaiement;
import ma.gestcom.common.enums.TypeDocument;
import ma.gestcom.common.enums.TypeReglement;
import ma.gestcom.modules.achat.service.DocumentAchatService;
import ma.gestcom.modules.achat.service.DocumentAchatWorkflowService;
import ma.gestcom.modules.article.dto.ArticleRequest;
import ma.gestcom.modules.article.service.ArticleService;
import ma.gestcom.modules.document.dto.DocumentLigneRequest;
import ma.gestcom.modules.document.dto.DocumentRequest;
import ma.gestcom.modules.document.dto.DocumentResponse;
import ma.gestcom.modules.document.dto.TransformationRequest;
import ma.gestcom.modules.reglement.dto.AffectationRequest;
import ma.gestcom.modules.reglement.dto.ReglementRequest;
import ma.gestcom.modules.reglement.dto.SoldeTiersResponse;
import ma.gestcom.modules.reglement.service.ReglementService;
import ma.gestcom.modules.reglement.service.SoldeService;
import ma.gestcom.modules.stock.dto.DepotRequest;
import ma.gestcom.modules.stock.service.DepotService;
import ma.gestcom.modules.stock.service.StockService;
import ma.gestcom.modules.tiers.dto.ClientRequest;
import ma.gestcom.modules.tiers.dto.FournisseurRequest;
import ma.gestcom.modules.tiers.service.ClientService;
import ma.gestcom.modules.tiers.service.FournisseurService;
import ma.gestcom.modules.vente.service.DocumentVenteService;
import ma.gestcom.modules.vente.service.DocumentVenteTransformationService;
import ma.gestcom.modules.vente.service.DocumentVenteWorkflowService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * End-to-end flow on a real PostgreSQL (Flyway migrations, constraints, locks):
 * réception → devis → BC → BL partiel → facture → règlement partiel → avoir avec retour.
 * Skipped automatically when Docker is not available.
 */
@SpringBootTest
@ActiveProfiles("test")
@Testcontainers(disabledWithoutDocker = true)
class FullFlowIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired DepotService depotService;
    @Autowired ArticleService articleService;
    @Autowired ClientService clientService;
    @Autowired FournisseurService fournisseurService;
    @Autowired StockService stockService;
    @Autowired DocumentAchatService achatService;
    @Autowired DocumentAchatWorkflowService achatWorkflow;
    @Autowired DocumentVenteService venteService;
    @Autowired DocumentVenteWorkflowService venteWorkflow;
    @Autowired DocumentVenteTransformationService venteTransformation;
    @Autowired ReglementService reglementService;
    @Autowired SoldeService soldeService;

    @Test
    void fluxCompletDeVente() {
        LocalDate today = LocalDate.now();
        Long depot = depotService.create(new DepotRequest("D1", "Dépôt principal", null, true, true)).id();
        Long article = articleService.create(new ArticleRequest("ART-1", "Écran 24\"", null, "U",
                new BigDecimal("100"), new BigDecimal("150"), new BigDecimal("20"), null, BigDecimal.ZERO, true,
                ArticleType.STOCKE)).id();
        Long client = clientService.create(new ClientRequest("CL-1", "Client Test", false, false, false, false, null,
                TypeClient.A_TERME, RisqueClient.NORMAL, "001122334455667", null, null, null, null, null,
                "Bd Anfa", "Maroc", "Casablanca", null, null, null, null, null, null, "MAD", null, null, null, null,
                null, null, null, null, TypeReglement.VIREMENT, 30, BigDecimal.ZERO, BigDecimal.ZERO,
                new BigDecimal("1000"), null, null, null, null, null, null, true)).id();
        Long fournisseur = fournisseurService.create(new FournisseurRequest("FR-1", "Fournisseur Test", null,
                TypeFournisseur.A_TERME, null, "Maroc", null, null, null, null, null, null, null, null, null, "MAD",
                null, null, null, null, true, List.of(), null, null, null, BigDecimal.ZERO, null, null, null, 0,
                false, false, null, false, null, 0, 0, null, null, null, BigDecimal.ZERO, 0, BigDecimal.ZERO,
                BigDecimal.ZERO, BigDecimal.ZERO, null, BigDecimal.ZERO, null, null, null, null)).id();

        // Réception: 20 at 100 then 10 at 130 -> PUMP 110
        valider(TypeDocument.RECEPTION, achatService.create(TypeDocument.RECEPTION,
                requete(fournisseur, depot, today, "20", "100")));
        valider(TypeDocument.RECEPTION, achatService.create(TypeDocument.RECEPTION,
                requete(fournisseur, depot, today, "10", "130")));
        assertThat(stockService.getQuantite(article, depot)).isEqualByComparingTo("30");
        assertThat(articleService.get(article).pump()).isEqualByComparingTo("110");

        // Devis 10 -> BC -> BL partiel 6
        DocumentResponse devis = venteWorkflow.valider(TypeDocument.DEVIS,
                venteService.create(TypeDocument.DEVIS, requete(client, depot, today, "10", null)).id());
        assertThat(devis.numero()).matches("DEV-\\d{4}-00001");
        assertThat(devis.totalTtc()).isEqualByComparingTo("1800.00");
        DocumentResponse bc = transformer(TypeDocument.DEVIS, devis.id(), TypeDocument.BON_COMMANDE, null);
        bc = venteWorkflow.valider(TypeDocument.BON_COMMANDE, bc.id());
        DocumentResponse bl = transformer(TypeDocument.BON_COMMANDE, bc.id(), TypeDocument.BON_LIVRAISON,
                List.of(new TransformationRequest.Ligne(bc.lignes().get(0).id(), new BigDecimal("6"))));
        bl = venteWorkflow.valider(TypeDocument.BON_LIVRAISON, bl.id());
        assertThat(stockService.getQuantite(article, depot)).isEqualByComparingTo("24");
        assertThat(venteService.get(TypeDocument.BON_COMMANDE, bc.id()).statut())
                .isEqualTo(StatutDocument.TRANSFORME_PARTIEL);
        assertThat(venteService.get(TypeDocument.BON_COMMANDE, bc.id()).lignes().get(0).quantiteRestante())
                .isEqualByComparingTo("4");

        // Facture from the BL: no second stock decrease; credit limit warning (1080 > 1000)
        DocumentResponse facture = transformer(TypeDocument.BON_LIVRAISON, bl.id(), TypeDocument.FACTURE, null);
        facture = venteWorkflow.valider(TypeDocument.FACTURE, facture.id());
        assertThat(stockService.getQuantite(article, depot)).isEqualByComparingTo("24");
        assertThat(facture.totalTtc()).isEqualByComparingTo("1080.00");
        assertThat(facture.warnings()).isNotEmpty();
        assertThat(facture.dateEcheance()).isEqualTo(today.plusDays(30));
        assertThat(venteService.get(TypeDocument.BON_LIVRAISON, bl.id()).statut()).isEqualTo(StatutDocument.TRANSFORME);

        // Règlement partiel
        reglementService.create(SensTiers.CLIENT, new ReglementRequest(client, today, TypeReglement.VIREMENT,
                new BigDecimal("400"), null, null, null, null,
                List.of(new AffectationRequest(facture.id(), new BigDecimal("400")))));
        DocumentResponse payee = venteService.get(TypeDocument.FACTURE, facture.id());
        assertThat(payee.statutPaiement()).isEqualTo(StatutPaiement.PARTIELLE);
        assertThat(payee.resteAPayer()).isEqualByComparingTo("680.00");

        // Avoir de 2 avec retour marchandise -> stock remonte
        DocumentResponse avoir = venteTransformation.transformer(TypeDocument.FACTURE, facture.id(),
                new TransformationRequest(TypeDocument.AVOIR, today, true,
                        List.of(new TransformationRequest.Ligne(facture.lignes().get(0).id(), new BigDecimal("2")))));
        avoir = venteWorkflow.valider(TypeDocument.AVOIR, avoir.id());
        assertThat(avoir.totalTtc()).isEqualByComparingTo("360.00");
        assertThat(stockService.getQuantite(article, depot)).isEqualByComparingTo("26");

        SoldeTiersResponse solde = soldeService.solde(SensTiers.CLIENT, client);
        // 1080 facturé - 360 avoir - 400 réglé
        assertThat(solde.solde()).isEqualByComparingTo("320.00");
        assertThat(solde.facturesNonSoldees()).hasSize(1);
    }

    private DocumentResponse transformer(TypeDocument type, Long id, TypeDocument cible,
                                         List<TransformationRequest.Ligne> lignes) {
        return venteTransformation.transformer(type, id, new TransformationRequest(cible, null, false, lignes));
    }

    private void valider(TypeDocument type, DocumentResponse doc) {
        achatWorkflow.valider(type, doc.id());
    }

    private DocumentRequest requete(Long tiers, Long depot, LocalDate date, String qte, String prix) {
        Long articleId = articleService.search(new ma.gestcom.modules.article.dto.ArticleFilter("ART-1", null, null,
                null), org.springframework.data.domain.PageRequest.of(0, 1)).content().get(0).id();
        return new DocumentRequest(tiers, date, depot, null, null, null, false, null,
                List.of(new DocumentLigneRequest(articleId, null, new BigDecimal(qte),
                        prix == null ? null : new BigDecimal(prix), null, null, null)));
    }
}
