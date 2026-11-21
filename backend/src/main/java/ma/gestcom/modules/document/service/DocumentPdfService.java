package ma.gestcom.modules.document.service;

import ma.gestcom.modules.document.dto.DocumentResponse;

/**
 * Renders any commercial document (sale or purchase) as a printable PDF.
 */
public interface DocumentPdfService {

    /**
     * @param tiersLabel "Client" or "Fournisseur"
     */
    byte[] generer(DocumentResponse document, String tiersLabel);
}
