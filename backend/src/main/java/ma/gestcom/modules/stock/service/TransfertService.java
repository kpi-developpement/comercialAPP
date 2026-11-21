package ma.gestcom.modules.stock.service;

import ma.gestcom.common.dto.PageResponse;
import ma.gestcom.modules.stock.dto.OperationFilter;
import ma.gestcom.modules.stock.dto.TransfertRequest;
import ma.gestcom.modules.stock.dto.TransfertResponse;
import org.springframework.data.domain.Pageable;

public interface TransfertService {

    PageResponse<TransfertResponse> search(OperationFilter filter, Pageable pageable);

    TransfertResponse get(Long id);

    TransfertResponse create(TransfertRequest request);

    TransfertResponse update(Long id, TransfertRequest request);

    /** Moves the goods: TRANSFERT_SORTIE from the source, TRANSFERT_ENTREE into the destination (PUMP unchanged). */
    TransfertResponse valider(Long id);

    void delete(Long id);
}
