package ma.gestcom.modules.achat.service.impl;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import ma.gestcom.common.enums.TypeDocument;
import ma.gestcom.common.exception.BusinessException;
import ma.gestcom.common.exception.ErrorCodes;
import ma.gestcom.modules.achat.entity.DocumentAchat;
import ma.gestcom.modules.achat.entity.DocumentAchatLigne;
import ma.gestcom.modules.achat.repository.DocumentAchatRepository;
import ma.gestcom.modules.achat.service.DocumentAchatTransformationService;
import ma.gestcom.modules.document.dto.DocumentResponse;
import ma.gestcom.modules.document.dto.GroupementRequest;
import ma.gestcom.modules.document.dto.TransformationRequest;
import ma.gestcom.modules.document.service.DocumentCalculator;
import ma.gestcom.modules.document.service.DocumentLigneFactory;
import ma.gestcom.modules.document.service.DocumentLinkService;
import ma.gestcom.modules.document.service.DocumentWorkflow;
import ma.gestcom.modules.document.service.TransformationPlanner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DocumentAchatTransformationServiceImpl implements DocumentAchatTransformationService {

    private final DocumentAchatRepository repository;
    private final DocumentAchatHelper helper;
    private final DocumentLinkService linkService;

    @Override
    public DocumentResponse transformer(TypeDocument type, Long id, TransformationRequest request) {
        DocumentAchat source = helper.find(type, id);
        TransformationPlanner.verifier(source, request);
        Map<Long, BigDecimal> quantites = TransformationPlanner.quantites(source, request.lignes());

        DocumentAchat cible = helper.nouveauDepuis(source, request.cible(), request.dateDocument());
        cible.setRetourMarchandise(DocumentWorkflow.estAvoir(request.cible()) && request.retourMarchandise());
        int ordre = 1;
        for (DocumentAchatLigne ligne : source.getLignes()) {
            BigDecimal q = quantites.get(ligne.getId());
            if (q != null) {
                cible.addLigne(DocumentLigneFactory.copier(DocumentAchatLigne::new, ligne, q, ordre++));
            }
        }
        return enregistrer(cible);
    }

    @Override
    public DocumentResponse grouperReceptions(GroupementRequest request) {
        List<DocumentAchat> receptions = new LinkedHashSet<>(request.documentIds()).stream()
                .map(id -> helper.find(TypeDocument.RECEPTION, id))
                .toList();
        DocumentAchat premiere = receptions.get(0);
        if (receptions.stream().anyMatch(r -> !r.getTiersId().equals(premiere.getTiersId()))) {
            throw new BusinessException(ErrorCodes.GROUPING_INVALID,
                    "Les réceptions à regrouper doivent concerner le même fournisseur");
        }
        DocumentAchat facture = helper.nouveauDepuis(premiere, TypeDocument.FACTURE_FOURNISSEUR,
                request.dateDocument());
        int ordre = 1;
        for (DocumentAchat reception : receptions) {
            if (!reception.getStatut().isTransformable()) {
                throw new BusinessException(ErrorCodes.DOCUMENT_INVALID_STATE,
                        "La réception " + reception.getNumero() + " n'est pas facturable");
            }
            for (DocumentAchatLigne ligne : reception.getLignes()) {
                if (ligne.getQuantiteRestante().signum() > 0) {
                    facture.addLigne(DocumentLigneFactory.copier(DocumentAchatLigne::new, ligne,
                            ligne.getQuantiteRestante(), ordre++));
                }
            }
        }
        if (facture.getLignes().isEmpty()) {
            throw new BusinessException(ErrorCodes.DOCUMENT_EMPTY, "Aucune quantité restant à facturer");
        }
        return enregistrer(facture);
    }

    private DocumentResponse enregistrer(DocumentAchat cible) {
        DocumentCalculator.calculerTotaux(cible);
        linkService.consommer(cible, helper.chargeurLignes());
        return helper.toResponse(repository.save(cible), List.of());
    }
}
