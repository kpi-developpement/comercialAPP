package ma.gestcom.modules.achat.service;

import ma.gestcom.common.enums.TypeDocument;
import ma.gestcom.modules.document.dto.DocumentResponse;
import ma.gestcom.modules.document.dto.GroupementRequest;
import ma.gestcom.modules.document.dto.TransformationRequest;

public interface DocumentAchatTransformationService {

    DocumentResponse transformer(TypeDocument type, Long id, TransformationRequest request);

    /** Groups several réceptions of the same supplier into one draft facture fournisseur. */
    DocumentResponse grouperReceptions(GroupementRequest request);
}
