package ma.gestcom.modules.vente.service;

import ma.gestcom.common.enums.TypeDocument;
import ma.gestcom.modules.document.dto.DocumentResponse;
import ma.gestcom.modules.document.dto.GroupementRequest;
import ma.gestcom.modules.document.dto.TransformationRequest;

/**
 * Creates the next document of the sales chain from validated documents (fully or partially).
 */
public interface DocumentVenteTransformationService {

    /** Creates a draft of {@code request.cible()} from the remaining quantities of the source document. */
    DocumentResponse transformer(TypeDocument type, Long id, TransformationRequest request);

    /** Groups several bons de livraison of the same client into one draft facture. */
    DocumentResponse grouperLivraisons(GroupementRequest request);
}
