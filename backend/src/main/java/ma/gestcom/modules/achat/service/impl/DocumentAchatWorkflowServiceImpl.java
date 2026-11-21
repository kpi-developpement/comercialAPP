package ma.gestcom.modules.achat.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
import ma.gestcom.common.util.MoneyUtil;
import ma.gestcom.modules.achat.entity.DocumentAchat;
import ma.gestcom.modules.achat.entity.DocumentAchatLigne;
import ma.gestcom.modules.achat.repository.DocumentAchatRepository;
import ma.gestcom.modules.achat.service.DocumentAchatWorkflowService;
import ma.gestcom.modules.document.dto.DocumentResponse;
import ma.gestcom.modules.document.dto.DocumentTypeRef;
import ma.gestcom.modules.document.service.DocumentCalculator;
import ma.gestcom.modules.document.service.DocumentWorkflow;
import ma.gestcom.modules.document.service.StockEffet;
import ma.gestcom.modules.numerotation.service.NumerotationService;
import ma.gestcom.modules.stock.dto.StockOperation;
import ma.gestcom.modules.stock.service.DepotService;
import ma.gestcom.modules.stock.service.StockService;
import ma.gestcom.modules.tiers.dto.TiersInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DocumentAchatWorkflowServiceImpl implements DocumentAchatWorkflowService {

    private final DocumentAchatRepository repository;
    private final DocumentAchatHelper helper;
    private final NumerotationService numerotationService;
    private final StockService stockService;
    private final DepotService depotService;

    @Override
    public DocumentResponse valider(TypeDocument type, Long id) {
        DocumentAchat doc = helper.findBrouillon(type, id);
        if (doc.getLignes().isEmpty()) {
            throw new BusinessException(ErrorCodes.DOCUMENT_EMPTY, "Le document ne contient aucune ligne");
        }
        TiersInfo fournisseur = helper.fournisseurActif(doc.getTiersId());
        depotService.requireActif(doc.getDepotId());
        DocumentCalculator.calculerTotaux(doc);

        Map<Long, TypeDocument> typesSources = typesSources(doc);
        Map<DocumentAchatLigne, StockEffet> effets = new LinkedHashMap<>();
        Map<Long, BigDecimal> besoins = new LinkedHashMap<>();
        for (DocumentAchatLigne l : doc.getLignes()) {
            StockEffet effet = DocumentWorkflow.effetStock(type, l.getSourceDocumentId() == null ? null : typesSources.get(l.getSourceDocumentId()),
                    doc.isRetourMarchandise());
            effets.put(l, effet);
            if (effet == StockEffet.SORTIE && l.isArticleStocke() && l.getArticleId() != null) {
                besoins.merge(l.getArticleId(), l.getQuantite(), BigDecimal::add);
            }
        }
        stockService.verifierDisponibilite(doc.getDepotId(), besoins);

        String numero = numerotationService.nextNumero(type, doc.getDateDocument());
        doc.setNumero(numero);
        for (DocumentAchatLigne l : doc.getLignes()) {
            l.setQuantiteRestante(l.getQuantite());
            l.setCoutUnitaire(coutNet(l));
            StockEffet effet = effets.get(l);
            if (l.getArticleId() == null || !l.isArticleStocke() || l.isImpacteStock() || effet == StockEffet.AUCUN) {
                continue;
            }
            if (effet == StockEffet.ENTREE) {
                stockService.entree(new StockOperation(l.getArticleId(), doc.getDepotId(), l.getQuantite(),
                        l.getCoutUnitaire(), TypeMouvement.ENTREE_ACHAT, true, type, doc.getId(), numero,
                        type.getLibelle() + " " + numero));
            } else {
                stockService.sortie(new StockOperation(l.getArticleId(), doc.getDepotId(), l.getQuantite(),
                        null, TypeMouvement.RETOUR_FOURNISSEUR, false, type, doc.getId(), numero,
                        type.getLibelle() + " " + numero));
            }
            l.setImpacteStock(true);
        }
        if (type == TypeDocument.FACTURE_FOURNISSEUR) {
            if (doc.getDateEcheance() == null) {
                doc.setDateEcheance(doc.getDateDocument().plusDays(fournisseur.delaiPaiement()));
            }
            doc.setMontantRegle(BigDecimal.ZERO);
            doc.setStatutPaiement(StatutPaiement.NON_PAYEE);
        }
        doc.setStatut(StatutDocument.VALIDE);
        return helper.toResponse(doc, List.of());
    }

    @Override
    public DocumentResponse annuler(TypeDocument type, Long id) {
        DocumentAchat doc = helper.find(type, id);
        if (!DocumentWorkflow.estAnnulable(type)) {
            throw new BusinessException(ErrorCodes.DOCUMENT_INVALID_STATE,
                    "Ce document ne peut pas être annulé : établissez un avoir fournisseur");
        }
        if (doc.getStatut() != StatutDocument.VALIDE) {
            throw new BusinessException(ErrorCodes.DOCUMENT_INVALID_STATE,
                    "Seul un document validé et non transformé peut être annulé (brouillon : supprimez-le)");
        }
        doc.getLignes().forEach(l -> l.setQuantiteRestante(BigDecimal.ZERO));
        doc.setStatut(StatutDocument.ANNULE);
        return helper.toResponse(doc, List.of());
    }

    /** Net purchase cost per unit (after line discount): the input of the PUMP. */
    static BigDecimal coutNet(DocumentAchatLigne l) {
        if (l.getQuantite().signum() == 0) {
            return BigDecimal.ZERO;
        }
        return l.getMontantHt().divide(l.getQuantite(), MoneyUtil.QTY_SCALE, RoundingMode.HALF_UP);
    }

    private Map<Long, TypeDocument> typesSources(DocumentAchat doc) {
        Set<Long> ids = doc.getLignes().stream().map(DocumentAchatLigne::getSourceDocumentId)
                .filter(Objects::nonNull).collect(Collectors.toSet());
        return ids.isEmpty() ? Map.of() : repository.findTypes(ids).stream()
                .collect(Collectors.toMap(DocumentTypeRef::id, DocumentTypeRef::type));
    }
}
