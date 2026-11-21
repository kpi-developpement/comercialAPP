package ma.gestcom.modules.reglement.service;

import ma.gestcom.common.dto.PageResponse;
import ma.gestcom.common.enums.SensTiers;
import ma.gestcom.modules.document.dto.DocumentSummaryResponse;
import ma.gestcom.modules.reglement.dto.EcheancierFilter;
import ma.gestcom.modules.reglement.dto.SoldeTiersResponse;
import org.springframework.data.domain.Pageable;

/**
 * Tiers balances and payment schedule.
 */
public interface SoldeService {

    SoldeTiersResponse solde(SensTiers sens, Long tiersId);

    /** Invoices not fully paid, ordered by due date. */
    PageResponse<DocumentSummaryResponse> echeancier(SensTiers sens, EcheancierFilter filter, Pageable pageable);
}
