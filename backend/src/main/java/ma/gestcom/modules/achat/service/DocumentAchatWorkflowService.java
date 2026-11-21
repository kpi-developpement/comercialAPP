package ma.gestcom.modules.achat.service;

import ma.gestcom.common.enums.TypeDocument;
import ma.gestcom.modules.document.dto.DocumentResponse;

/**
 * Validation (numbering, stock entries with PUMP recalculation) and cancellation of purchase documents.
 */
public interface DocumentAchatWorkflowService {

    DocumentResponse valider(TypeDocument type, Long id);

    DocumentResponse annuler(TypeDocument type, Long id);
}
