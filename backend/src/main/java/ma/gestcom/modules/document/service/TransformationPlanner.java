package ma.gestcom.modules.document.service;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ma.gestcom.common.exception.BusinessException;
import ma.gestcom.common.exception.ErrorCodes;
import ma.gestcom.common.exception.NotFoundException;
import ma.gestcom.modules.document.dto.TransformationRequest;
import ma.gestcom.modules.document.entity.AbstractDocument;
import ma.gestcom.modules.document.entity.AbstractDocumentLigne;

/**
 * Validates a transformation request and computes the quantity to take from each source line.
 */
public final class TransformationPlanner {

    private TransformationPlanner() {
    }

    /** Throws when the source cannot be transformed into {@code cible}. */
    public static void verifier(AbstractDocument<?> source, TransformationRequest request) {
        if (!DocumentWorkflow.peutTransformer(source.getType(), request.cible())) {
            throw new BusinessException(ErrorCodes.TRANSFORMATION_NOT_ALLOWED,
                    source.getType().getLibelle() + " ne peut pas être transformé en " + request.cible().getLibelle());
        }
        if (!source.getStatut().isTransformable()) {
            throw new BusinessException(ErrorCodes.DOCUMENT_INVALID_STATE,
                    "Le document doit être validé et avoir des quantités restantes pour être transformé");
        }
    }

    /**
     * @return source line id -> quantity to transform (only positive quantities), in line order
     */
    public static Map<Long, BigDecimal> quantites(AbstractDocument<?> source, List<TransformationRequest.Ligne> demandes) {
        Map<Long, BigDecimal> result = new LinkedHashMap<>();
        if (demandes == null || demandes.isEmpty()) {
            for (AbstractDocumentLigne l : source.getLignes()) {
                if (l.getQuantiteRestante().signum() > 0) {
                    result.put(l.getId(), l.getQuantiteRestante());
                }
            }
        } else {
            Map<Long, AbstractDocumentLigne> lignes = new LinkedHashMap<>();
            source.getLignes().forEach(l -> lignes.put(l.getId(), l));
            for (TransformationRequest.Ligne d : demandes) {
                if (!lignes.containsKey(d.ligneId())) {
                    throw new NotFoundException("Ligne du document source", d.ligneId());
                }
                if (d.quantite().signum() > 0) {
                    result.put(d.ligneId(), d.quantite());
                }
            }
        }
        if (result.isEmpty()) {
            throw new BusinessException(ErrorCodes.DOCUMENT_EMPTY, "Aucune quantité à transformer");
        }
        return result;
    }
}
