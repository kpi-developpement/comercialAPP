package ma.gestcom.modules.vente.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import ma.gestcom.common.enums.StatutDocument;
import ma.gestcom.common.enums.StatutPaiement;
import ma.gestcom.common.enums.TypeDocument;
import ma.gestcom.common.enums.TypeMouvement;
import ma.gestcom.common.exception.BusinessException;
import ma.gestcom.common.exception.ErrorCodes;
import ma.gestcom.common.util.FormatUtil;
import ma.gestcom.modules.article.dto.ArticleInfo;
import ma.gestcom.modules.article.service.ArticleService;
import ma.gestcom.modules.document.dto.DocumentResponse;
import ma.gestcom.modules.document.dto.DocumentTypeRef;
import ma.gestcom.modules.document.dto.TiersEncours;
import ma.gestcom.modules.document.service.DocumentCalculator;
import ma.gestcom.modules.document.service.DocumentWorkflow;
import ma.gestcom.modules.document.service.StockEffet;
import ma.gestcom.modules.numerotation.service.NumerotationService;
import ma.gestcom.modules.stock.dto.StockOperation;
import ma.gestcom.modules.stock.service.DepotService;
import ma.gestcom.modules.stock.service.StockService;
import ma.gestcom.modules.tiers.dto.TiersInfo;
import ma.gestcom.modules.vente.entity.DocumentVente;
import ma.gestcom.modules.vente.entity.DocumentVenteLigne;
import ma.gestcom.modules.vente.repository.DocumentVenteRepository;
import ma.gestcom.modules.vente.service.DocumentVenteService;
import ma.gestcom.modules.vente.service.DocumentVenteWorkflowService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DocumentVenteWorkflowServiceImpl implements DocumentVenteWorkflowService {

    private static final Set<TypeDocument> CONTROLE_PLAFOND =
            Set.of(TypeDocument.BON_COMMANDE, TypeDocument.BON_LIVRAISON, TypeDocument.FACTURE);

    private final DocumentVenteRepository repository;
    private final DocumentVenteHelper helper;
    private final DocumentVenteService documentService;
    private final NumerotationService numerotationService;
    private final StockService stockService;
    private final DepotService depotService;
    private final ArticleService articleService;

    @Override
    public DocumentResponse valider(TypeDocument type, Long id) {
        DocumentVente doc = helper.findBrouillon(type, id);
        if (doc.getLignes().isEmpty()) {
            throw new BusinessException(ErrorCodes.DOCUMENT_EMPTY, "Le document ne contient aucune ligne");
        }
        TiersInfo client = helper.clientActif(doc.getTiersId(), type);
        depotService.requireActif(doc.getDepotId());
        DocumentCalculator.calculerTotaux(doc);

        Map<Long, TypeDocument> typesSources = typesSources(doc);
        Map<DocumentVenteLigne, StockEffet> effets = new LinkedHashMap<>();
        Map<Long, BigDecimal> besoins = new LinkedHashMap<>();
        for (DocumentVenteLigne l : doc.getLignes()) {
            StockEffet effet = DocumentWorkflow.effetStock(type, l.getSourceDocumentId() == null ? null : typesSources.get(l.getSourceDocumentId()),
                    doc.isRetourMarchandise());
            effets.put(l, effet);
            if (effet == StockEffet.SORTIE && l.isArticleStocke() && l.getArticleId() != null) {
                besoins.merge(l.getArticleId(), l.getQuantite(), BigDecimal::add);
            }
        }
        stockService.verifierDisponibilite(doc.getDepotId(), besoins);
        List<String> warnings = controlePlafond(doc, client);

        String numero = numerotationService.nextNumero(type, doc.getDateDocument());
        doc.setNumero(numero);
        Map<Long, ArticleInfo> articles = articleService.getInfos(doc.getLignes().stream()
                .map(DocumentVenteLigne::getArticleId).filter(Objects::nonNull).collect(Collectors.toSet()));
        for (DocumentVenteLigne l : doc.getLignes()) {
            l.setQuantiteRestante(l.getQuantite());
            ArticleInfo article = l.getArticleId() == null ? null : articles.get(l.getArticleId());
            if (article != null) {
                l.setCoutUnitaire(article.pump());
            }
            StockEffet effet = effets.get(l);
            if (article == null || !l.isArticleStocke() || l.isImpacteStock() || effet == StockEffet.AUCUN) {
                continue;
            }
            boolean sortie = effet == StockEffet.SORTIE;
            StockOperation op = new StockOperation(l.getArticleId(), doc.getDepotId(), l.getQuantite(),
                    article.pump(), sortie ? TypeMouvement.SORTIE_VENTE : TypeMouvement.RETOUR_CLIENT, false,
                    type, doc.getId(), numero, type.getLibelle() + " " + numero);
            if (sortie) {
                stockService.sortie(op);
            } else {
                stockService.entree(op);
            }
            l.setImpacteStock(true);
        }
        if (type == TypeDocument.FACTURE) {
            if (doc.getDateEcheance() == null) {
                doc.setDateEcheance(doc.getDateDocument().plusDays(client.delaiPaiement()));
            }
            doc.setMontantRegle(BigDecimal.ZERO);
            doc.setStatutPaiement(StatutPaiement.NON_PAYEE);
        }
        doc.setStatut(StatutDocument.VALIDE);
        return helper.toResponse(doc, warnings);
    }

    @Override
    public DocumentResponse annuler(TypeDocument type, Long id) {
        DocumentVente doc = helper.find(type, id);
        if (!DocumentWorkflow.estAnnulable(type)) {
            throw new BusinessException(ErrorCodes.DOCUMENT_INVALID_STATE,
                    "Ce document ne peut pas être annulé : établissez un avoir");
        }
        if (doc.getStatut() != StatutDocument.VALIDE) {
            throw new BusinessException(ErrorCodes.DOCUMENT_INVALID_STATE,
                    "Seul un document validé et non transformé peut être annulé (brouillon : supprimez-le)");
        }
        doc.getLignes().forEach(l -> l.setQuantiteRestante(BigDecimal.ZERO));
        doc.setStatut(StatutDocument.ANNULE);
        return helper.toResponse(doc, List.of());
    }

    private Map<Long, TypeDocument> typesSources(DocumentVente doc) {
        Set<Long> ids = doc.getLignes().stream().map(DocumentVenteLigne::getSourceDocumentId)
                .filter(Objects::nonNull).collect(Collectors.toSet());
        return ids.isEmpty() ? Map.of() : repository.findTypes(ids).stream()
                .collect(Collectors.toMap(DocumentTypeRef::id, DocumentTypeRef::type));
    }

    /** Warns (never blocks) when the client's outstanding amount would exceed its credit limit. */
    private List<String> controlePlafond(DocumentVente doc, TiersInfo client) {
        List<String> warnings = new ArrayList<>();
        BigDecimal plafond = client.plafondCredit();
        if (!CONTROLE_PLAFOND.contains(doc.getType()) || plafond == null || plafond.signum() <= 0) {
            return warnings;
        }
        TiersEncours e = documentService.encours(client.id());
        BigDecimal encours = e.totalFactures().subtract(e.totalRegle()).subtract(e.totalAvoirs());
        if (encours.add(doc.getTotalTtc()).compareTo(plafond) > 0) {
            warnings.add("Plafond de crédit dépassé : encours " + FormatUtil.montant(encours) + " + document "
                    + FormatUtil.montant(doc.getTotalTtc()) + " > plafond " + FormatUtil.montant(plafond));
        }
        return warnings;
    }
}
