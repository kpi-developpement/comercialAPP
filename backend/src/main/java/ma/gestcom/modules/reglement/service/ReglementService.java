package ma.gestcom.modules.reglement.service;

import ma.gestcom.common.dto.PageResponse;
import ma.gestcom.common.enums.SensTiers;
import ma.gestcom.common.enums.StatutReglement;
import ma.gestcom.modules.reglement.dto.AffectationsRequest;
import ma.gestcom.modules.reglement.dto.ReglementFilter;
import ma.gestcom.modules.reglement.dto.ReglementRequest;
import ma.gestcom.modules.reglement.dto.ReglementResponse;
import org.springframework.data.domain.Pageable;

public interface ReglementService {

    PageResponse<ReglementResponse> search(SensTiers sens, ReglementFilter filter, Pageable pageable);

    ReglementResponse get(SensTiers sens, Long id);

    /** Records the payment and allocates it to the given invoices (each allocation ≤ invoice remaining amount). */
    ReglementResponse create(SensTiers sens, ReglementRequest request);

    ReglementResponse affecter(SensTiers sens, Long id, AffectationsRequest request);

    /** Chèque / effet lifecycle. Going IMPAYE gives the allocated amounts back to the invoices. */
    ReglementResponse changerStatut(SensTiers sens, Long id, StatutReglement statut);

    /** Deletes a payment entered by mistake and releases its allocations. */
    void delete(SensTiers sens, Long id);
}
