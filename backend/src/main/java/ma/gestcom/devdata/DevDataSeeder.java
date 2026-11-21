package ma.gestcom.devdata;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.gestcom.common.enums.SensTiers;
import ma.gestcom.common.enums.StatutReglement;
import ma.gestcom.common.enums.TypeDocument;
import ma.gestcom.common.enums.TypeReglement;
import ma.gestcom.modules.achat.service.DocumentAchatService;
import ma.gestcom.modules.achat.service.DocumentAchatTransformationService;
import ma.gestcom.modules.achat.service.DocumentAchatWorkflowService;
import ma.gestcom.modules.document.dto.DocumentLigneRequest;
import ma.gestcom.modules.document.dto.DocumentRequest;
import ma.gestcom.modules.document.dto.DocumentResponse;
import ma.gestcom.modules.document.dto.GroupementRequest;
import ma.gestcom.modules.document.dto.TransformationRequest;
import ma.gestcom.modules.reglement.dto.AffectationRequest;
import ma.gestcom.modules.reglement.dto.ReglementRequest;
import ma.gestcom.modules.reglement.dto.ReglementResponse;
import ma.gestcom.modules.reglement.service.ReglementService;
import ma.gestcom.modules.stock.dto.TransfertRequest;
import ma.gestcom.modules.stock.service.TransfertService;
import ma.gestcom.modules.vente.service.DocumentVenteService;
import ma.gestcom.modules.vente.service.DocumentVenteTransformationService;
import ma.gestcom.modules.vente.service.DocumentVenteWorkflowService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * DEV ONLY. Generates purchases, stock, a year of sales and payments through the real business services,
 * so that the dashboard, stock and échéancier show coherent data. Runs once, when no sales document exists.
 */
@Slf4j
@Component
@Profile("dev")
@Order(100)
@RequiredArgsConstructor
public class DevDataSeeder implements ApplicationRunner {

    private final JdbcTemplate jdbc;
    private final DocumentAchatService achatService;
    private final DocumentAchatWorkflowService achatWorkflow;
    private final DocumentAchatTransformationService achatTransformation;
    private final DocumentVenteService venteService;
    private final DocumentVenteWorkflowService venteWorkflow;
    private final DocumentVenteTransformationService venteTransformation;
    private final ReglementService reglementService;
    private final TransfertService transfertService;

    private final Random random = new Random(42);

    @Override
    public void run(ApplicationArguments args) {
        Long ventes = jdbc.queryForObject("select count(*) from document_vente", Long.class);
        Long clients = jdbc.queryForObject("select count(*) from client", Long.class);
        if (ventes == null || ventes > 0 || clients == null || clients == 0) {
            return;
        }
        log.info("Génération des données de démonstration (achats, stock, ventes, règlements)...");
        Long casa = id("select id from depot where code = 'CASA'");
        Long rabat = id("select id from depot where code = 'RABAT'");
        Long tanger = id("select id from depot where code = 'TANGER'");
        approvisionner(casa, rabat);
        transfert(casa, tanger);
        ventes(casa);
        log.info("Données de démonstration générées");
    }

    private void approvisionner(Long casa, Long rabat) {
        LocalDate debut = LocalDate.now().minusMonths(12).withDayOfMonth(2);
        List<Map<String, Object>> fournisseurs = jdbc.queryForList("select id, code from fournisseur order by code");
        Map<String, String> familleParFournisseur = Map.of(
                "FR-001", "INFO-PC,INFO-PER", "FR-002", "BUR-PAP,BUR-ECR", "FR-003", "MOB-SIE,MOB-BUR",
                "FR-004", "INFO-RES", "FR-005", "BUR-ECR");
        for (Map<String, Object> f : fournisseurs) {
            Long fournisseurId = ((Number) f.get("id")).longValue();
            String familles = familleParFournisseur.get((String) f.get("code"));
            List<Map<String, Object>> articles = articlesDesFamilles(familles);
            if (articles.isEmpty()) {
                continue;
            }
            List<DocumentLigneRequest> lignesCasa = new ArrayList<>();
            List<DocumentLigneRequest> lignesRabat = new ArrayList<>();
            for (Map<String, Object> a : articles) {
                Long articleId = ((Number) a.get("id")).longValue();
                BigDecimal prix = (BigDecimal) a.get("prix_achat_ht");
                boolean consommable = ((String) a.get("famille")).startsWith("BUR");
                lignesCasa.add(ligne(articleId, consommable ? 400 : 45, null));
                lignesRabat.add(ligne(articleId, consommable ? 60 : 8, prix.multiply(new BigDecimal("1.04"))));
            }
            // BC fournisseur -> réception -> facture fournisseur (payée)
            DocumentResponse bcf = achatService.create(TypeDocument.BC_FOURNISSEUR,
                    requete(fournisseurId, debut, casa, lignesCasa));
            achatWorkflow.valider(TypeDocument.BC_FOURNISSEUR, bcf.id());
            DocumentResponse rec = achatTransformation.transformer(TypeDocument.BC_FOURNISSEUR, bcf.id(),
                    new TransformationRequest(TypeDocument.RECEPTION, debut.plusDays(5), false, null));
            achatWorkflow.valider(TypeDocument.RECEPTION, rec.id());
            DocumentResponse faf = achatTransformation.transformer(TypeDocument.RECEPTION, rec.id(),
                    new TransformationRequest(TypeDocument.FACTURE_FOURNISSEUR, debut.plusDays(6), false, null));
            faf = achatWorkflow.valider(TypeDocument.FACTURE_FOURNISSEUR, faf.id());
            payer(SensTiers.FOURNISSEUR, fournisseurId, faf, faf.totalTtc(), debut.plusDays(40), TypeReglement.VIREMENT);

            // Direct réception at Rabat, slightly more expensive (PUMP changes), invoice left unpaid
            DocumentResponse recRabat = achatService.create(TypeDocument.RECEPTION,
                    requete(fournisseurId, LocalDate.now().minusMonths(3), rabat, lignesRabat));
            achatWorkflow.valider(TypeDocument.RECEPTION, recRabat.id());
            DocumentResponse fafRabat = achatTransformation.grouperReceptions(
                    new GroupementRequest(List.of(recRabat.id()), LocalDate.now().minusMonths(3).plusDays(2)));
            achatWorkflow.valider(TypeDocument.FACTURE_FOURNISSEUR, fafRabat.id());
        }
    }

    private void transfert(Long casa, Long tanger) {
        List<TransfertRequest.Ligne> lignes = jdbc.queryForList(
                        "select id from article where type = 'STOCKE' and code like 'PAP-%' order by code", Long.class)
                .stream().map(id -> new TransfertRequest.Ligne(id, new BigDecimal("20"))).toList();
        var t = transfertService.create(new TransfertRequest(casa, tanger, LocalDate.now().minusMonths(2),
                "Réassort dépôt Tanger", lignes));
        transfertService.valider(t.id());
    }

    private void ventes(Long casa) {
        List<Long> clients = jdbc.queryForList("select id from client order by code", Long.class);
        List<Map<String, Object>> articles = jdbc.queryForList(
                "select a.id, a.code, a.type from article a order by a.code");
        for (int mois = 11; mois >= 0; mois--) {
            int nbVentes = 3 + random.nextInt(3);
            for (int v = 0; v < nbVentes; v++) {
                Long clientId = clients.get(random.nextInt(clients.size()));
                LocalDate date = LocalDate.now().minusMonths(mois).withDayOfMonth(1 + random.nextInt(25));
                if (date.isAfter(LocalDate.now())) {
                    date = LocalDate.now();
                }
                try {
                    vente(clientId, casa, date, lignesAleatoires(articles), mois);
                } catch (RuntimeException e) {
                    log.warn("Vente de démonstration ignorée : {}", e.getMessage());
                }
            }
        }
        avoirDemo();
    }

    private void vente(Long clientId, Long depot, LocalDate date, List<DocumentLigneRequest> lignes, int mois) {
        int scenario = random.nextInt(10);
        DocumentResponse facture;
        if (scenario < 5) {
            // Full chain: devis -> BC -> BL -> facture
            DocumentResponse devis = venteService.create(TypeDocument.DEVIS, requete(clientId, date, depot, lignes));
            venteWorkflow.valider(TypeDocument.DEVIS, devis.id());
            DocumentResponse bc = venteTransformation.transformer(TypeDocument.DEVIS, devis.id(),
                    new TransformationRequest(TypeDocument.BON_COMMANDE, date.plusDays(1), false, null));
            venteWorkflow.valider(TypeDocument.BON_COMMANDE, bc.id());
            DocumentResponse bl = venteTransformation.transformer(TypeDocument.BON_COMMANDE, bc.id(),
                    new TransformationRequest(TypeDocument.BON_LIVRAISON, date.plusDays(2), false, null));
            venteWorkflow.valider(TypeDocument.BON_LIVRAISON, bl.id());
            facture = venteTransformation.transformer(TypeDocument.BON_LIVRAISON, bl.id(),
                    new TransformationRequest(TypeDocument.FACTURE, date.plusDays(3), false, null));
        } else if (scenario < 8) {
            // Two partial deliveries grouped into one invoice
            DocumentResponse bc = venteService.create(TypeDocument.BON_COMMANDE, requete(clientId, date, depot, lignes));
            bc = venteWorkflow.valider(TypeDocument.BON_COMMANDE, bc.id());
            var premiere = bc.lignes().get(0);
            BigDecimal partiel = premiere.quantite().divide(new BigDecimal("2"), 0, java.math.RoundingMode.DOWN);
            List<TransformationRequest.Ligne> partielles = bc.lignes().stream()
                    .map(l -> new TransformationRequest.Ligne(l.id(), l == premiere ? partiel : l.quantite()))
                    .toList();
            DocumentResponse bl1 = venteTransformation.transformer(TypeDocument.BON_COMMANDE, bc.id(),
                    new TransformationRequest(TypeDocument.BON_LIVRAISON, date.plusDays(1), false, partielles));
            venteWorkflow.valider(TypeDocument.BON_LIVRAISON, bl1.id());
            DocumentResponse bl2 = venteTransformation.transformer(TypeDocument.BON_COMMANDE, bc.id(),
                    new TransformationRequest(TypeDocument.BON_LIVRAISON, date.plusDays(4), false, null));
            venteWorkflow.valider(TypeDocument.BON_LIVRAISON, bl2.id());
            facture = venteTransformation.grouperLivraisons(
                    new GroupementRequest(List.of(bl1.id(), bl2.id()), date.plusDays(5)));
        } else {
            // Direct invoice (decreases stock itself)
            facture = venteService.create(TypeDocument.FACTURE, requete(clientId, date, depot, lignes));
        }
        if (facture.dateDocument().isAfter(LocalDate.now())) {
            return;
        }
        facture = venteWorkflow.valider(TypeDocument.FACTURE, facture.id());
        encaisser(clientId, facture, mois);
    }

    private void encaisser(Long clientId, DocumentResponse facture, int mois) {
        int tirage = random.nextInt(10);
        LocalDate date = facture.dateDocument().plusDays(10 + random.nextInt(20));
        if (date.isAfter(LocalDate.now())) {
            date = LocalDate.now();
        }
        if (mois >= 2 && tirage < 7) {
            payer(SensTiers.CLIENT, clientId, facture, facture.totalTtc(), date, TypeReglement.VIREMENT);
        } else if (tirage < 9 && mois >= 1) {
            BigDecimal moitie = facture.totalTtc().divide(new BigDecimal("2"), 2, java.math.RoundingMode.HALF_UP);
            ReglementResponse cheque = payer(SensTiers.CLIENT, clientId, facture, moitie, date, TypeReglement.CHEQUE);
            if (mois >= 3) {
                reglementService.changerStatut(SensTiers.CLIENT, cheque.id(), StatutReglement.ENCAISSE);
            }
        } else if (mois == 0 && tirage == 9) {
            payer(SensTiers.CLIENT, clientId, facture, facture.totalTtc(), LocalDate.now(), TypeReglement.EFFET);
        }
    }

    private ReglementResponse payer(SensTiers sens, Long tiersId, DocumentResponse facture, BigDecimal montant,
                                    LocalDate date, TypeReglement mode) {
        String reference = mode.isTitre() ? String.valueOf(1_000_000 + random.nextInt(8_999_999)) : null;
        LocalDate echeance = mode.isTitre() ? LocalDate.now().plusDays(random.nextInt(6)) : null;
        return reglementService.create(sens, new ReglementRequest(tiersId, date, mode, montant, reference,
                mode.isTitre() ? "Attijariwafa Bank" : null, echeance, null,
                List.of(new AffectationRequest(facture.id(), montant))));
    }

    /** Credit note with goods return on one of the oldest invoices. */
    private void avoirDemo() {
        List<Long> factures = jdbc.queryForList("select id from document_vente where type = 'FACTURE' "
                + "and statut = 'VALIDE' order by date_document limit 1", Long.class);
        if (factures.isEmpty()) {
            return;
        }
        DocumentResponse facture = venteService.get(TypeDocument.FACTURE, factures.get(0));
        var ligne = facture.lignes().get(0);
        DocumentResponse avoir = venteTransformation.transformer(TypeDocument.FACTURE, facture.id(),
                new TransformationRequest(TypeDocument.AVOIR, facture.dateDocument().plusDays(15), true,
                        List.of(new TransformationRequest.Ligne(ligne.id(), BigDecimal.ONE))));
        venteWorkflow.valider(TypeDocument.AVOIR, avoir.id());
    }

    private List<DocumentLigneRequest> lignesAleatoires(List<Map<String, Object>> articles) {
        List<DocumentLigneRequest> lignes = new ArrayList<>();
        int nb = 2 + random.nextInt(3);
        List<Integer> deja = new ArrayList<>();
        while (lignes.size() < nb) {
            int i = random.nextInt(articles.size());
            if (deja.contains(i)) {
                continue;
            }
            deja.add(i);
            Map<String, Object> a = articles.get(i);
            String code = (String) a.get("code");
            int qte = code.startsWith("PAP") || code.startsWith("ECR") ? 10 + random.nextInt(30)
                    : "SERVICE".equals(a.get("type")) ? 1 + random.nextInt(4) : 1 + random.nextInt(4);
            lignes.add(ligne(((Number) a.get("id")).longValue(), qte, null));
        }
        return lignes;
    }

    private List<Map<String, Object>> articlesDesFamilles(String familles) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (String famille : familles.split(",")) {
            result.addAll(jdbc.queryForList("select a.id, a.prix_achat_ht, f.code as famille from article a "
                    + "join famille f on f.id = a.famille_id where f.code = ? and a.type = 'STOCKE'", famille));
        }
        return result;
    }

    private static DocumentLigneRequest ligne(Long articleId, int quantite, BigDecimal prix) {
        return new DocumentLigneRequest(articleId, null, BigDecimal.valueOf(quantite), prix, null, null, null);
    }

    private static DocumentRequest requete(Long tiersId, LocalDate date, Long depot, List<DocumentLigneRequest> l) {
        return new DocumentRequest(tiersId, date, depot, null, null, null, false, null, l);
    }

    private Long id(String sql) {
        return jdbc.queryForObject(sql, Long.class);
    }
}
