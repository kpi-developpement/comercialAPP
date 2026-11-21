package ma.gestcom.modules.vente.service;

import ma.gestcom.common.enums.TypeDocument;
import ma.gestcom.modules.document.dto.DocumentResponse;

/**
 * Validation (numbering + stock movements) and cancellation of sales documents.
 */
public interface DocumentVenteWorkflowService {

    DocumentResponse valider(TypeDocument type, Long id);

    DocumentResponse annuler(TypeDocument type, Long id);
}
