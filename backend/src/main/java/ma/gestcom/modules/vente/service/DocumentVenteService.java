package ma.gestcom.modules.vente.service;

import java.math.BigDecimal;
import java.util.List;

import ma.gestcom.common.dto.PageResponse;
import ma.gestcom.common.enums.TypeDocument;
import ma.gestcom.modules.document.dto.DocumentFilter;
import ma.gestcom.modules.document.dto.DocumentRequest;
import ma.gestcom.modules.document.dto.DocumentResponse;
import ma.gestcom.modules.document.dto.DocumentSummaryResponse;
import ma.gestcom.modules.document.dto.FactureInfo;
import ma.gestcom.modules.document.dto.TiersEncours;
import org.springframework.data.domain.Pageable;

/**
 * Drafting and reading of sales documents, plus the invoice API used by the payment module.
 */
public interface DocumentVenteService {

    PageResponse<DocumentSummaryResponse> search(TypeDocument type, DocumentFilter filter, Pageable pageable);

    DocumentResponse get(TypeDocument type, Long id);

    DocumentResponse create(TypeDocument type, DocumentRequest request);

    DocumentResponse update(TypeDocument type, Long id, DocumentRequest request);

    void delete(TypeDocument type, Long id);

    byte[] pdf(TypeDocument type, Long id);

    // --- API for the payment module ---

    FactureInfo getFactureInfo(Long factureId);

    /** Adds {@code delta} (negative to cancel) to the settled amount of a validated invoice. */
    void appliquerReglement(Long factureId, BigDecimal delta);

    TiersEncours encours(Long clientId);

    List<DocumentSummaryResponse> facturesNonSoldees(Long clientId);
}
