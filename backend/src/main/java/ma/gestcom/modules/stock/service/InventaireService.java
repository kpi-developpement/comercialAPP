package ma.gestcom.modules.stock.service;

import ma.gestcom.common.dto.PageResponse;
import ma.gestcom.modules.stock.dto.InventaireComptageRequest;
import ma.gestcom.modules.stock.dto.InventaireCreateRequest;
import ma.gestcom.modules.stock.dto.InventaireResponse;
import ma.gestcom.modules.stock.dto.InventaireSummaryResponse;
import ma.gestcom.modules.stock.dto.OperationFilter;
import org.springframework.data.domain.Pageable;

public interface InventaireService {

    PageResponse<InventaireSummaryResponse> search(OperationFilter filter, Pageable pageable);

    InventaireResponse get(Long id);

    /** Opens a counting session pre-filled with every stocked article and its theoretical quantity. */
    InventaireResponse create(InventaireCreateRequest request);

    InventaireResponse saisirComptage(Long id, InventaireComptageRequest request);

    /** Numbers the session and generates AJUSTEMENT_INVENTAIRE movements for every counted gap. */
    InventaireResponse valider(Long id);

    void delete(Long id);
}
