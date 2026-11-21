package ma.gestcom.modules.vente.service.impl;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import ma.gestcom.common.enums.TypeDocument;
import ma.gestcom.common.exception.BusinessException;
import ma.gestcom.common.exception.ErrorCodes;
import ma.gestcom.modules.document.dto.DocumentResponse;
import ma.gestcom.modules.document.dto.GroupementRequest;
import ma.gestcom.modules.document.dto.TransformationRequest;
import ma.gestcom.modules.document.service.DocumentCalculator;
import ma.gestcom.modules.document.service.DocumentLigneFactory;
import ma.gestcom.modules.document.service.DocumentLinkService;
import ma.gestcom.modules.document.service.DocumentWorkflow;
import ma.gestcom.modules.document.service.TransformationPlanner;
import ma.gestcom.modules.vente.entity.DocumentVente;
import ma.gestcom.modules.vente.entity.DocumentVenteLigne;
import ma.gestcom.modules.vente.repository.DocumentVenteRepository;
import ma.gestcom.modules.vente.service.DocumentVenteTransformationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DocumentVenteTransformationServiceImpl implements DocumentVenteTransformationService {

    private final DocumentVenteRepository repository;
    private final DocumentVenteHelper helper;
    private final DocumentLinkService linkService;

    @Override
    public DocumentResponse transformer(TypeDocument type, Long id, TransformationRequest request) {
        DocumentVente source = helper.find(type, id);
        TransformationPlanner.verifier(source, request);
        Map<Long, BigDecimal> quantites = TransformationPlanner.quantites(source, request.lignes());

        DocumentVente cible = helper.nouveauDepuis(source, request.cible(), request.dateDocument());
        cible.setRetourMarchandise(DocumentWorkflow.estAvoir(request.cible()) && request.retourMarchandise());
        int ordre = 1;
        for (DocumentVenteLigne ligne : source.getLignes()) {
            BigDecimal q = quantites.get(ligne.getId());
            if (q != null) {
                cible.addLigne(DocumentLigneFactory.copier(DocumentVenteLigne::new, ligne, q, ordre++));
            }
        }
        return enregistrer(cible);
    }

    @Override
    public DocumentResponse grouperLivraisons(GroupementRequest request) {
        List<DocumentVente> livraisons = new LinkedHashSet<>(request.documentIds()).stream()
                .map(id -> helper.find(TypeDocument.BON_LIVRAISON, id))
                .toList();
        DocumentVente premier = livraisons.get(0);
        if (livraisons.stream().anyMatch(bl -> !bl.getTiersId().equals(premier.getTiersId()))) {
            throw new BusinessException(ErrorCodes.GROUPING_INVALID,
                    "Les bons de livraison à regrouper doivent concerner le même client");
        }
        DocumentVente facture = helper.nouveauDepuis(premier, TypeDocument.FACTURE, request.dateDocument());
        int ordre = 1;
        for (DocumentVente bl : livraisons) {
            if (!bl.getStatut().isTransformable()) {
                throw new BusinessException(ErrorCodes.DOCUMENT_INVALID_STATE,
                        "Le bon de livraison " + bl.getNumero() + " n'est pas facturable");
            }
            for (DocumentVenteLigne ligne : bl.getLignes()) {
                if (ligne.getQuantiteRestante().signum() > 0) {
                    facture.addLigne(DocumentLigneFactory.copier(DocumentVenteLigne::new, ligne,
                            ligne.getQuantiteRestante(), ordre++));
                }
            }
        }
        if (facture.getLignes().isEmpty()) {
            throw new BusinessException(ErrorCodes.DOCUMENT_EMPTY, "Aucune quantité restant à facturer");
        }
        return enregistrer(facture);
    }

    private DocumentResponse enregistrer(DocumentVente cible) {
        DocumentCalculator.calculerTotaux(cible);
        linkService.consommer(cible, helper.chargeurLignes());
        return helper.toResponse(repository.save(cible), List.of());
    }
}
