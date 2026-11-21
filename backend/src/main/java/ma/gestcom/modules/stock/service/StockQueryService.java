package ma.gestcom.modules.stock.service;

import java.util.List;

import ma.gestcom.common.dto.PageResponse;
import ma.gestcom.modules.stock.dto.EtatStockFilter;
import ma.gestcom.modules.stock.dto.EtatStockResponse;
import ma.gestcom.modules.stock.dto.MouvementFilter;
import ma.gestcom.modules.stock.dto.MouvementResponse;
import ma.gestcom.modules.stock.dto.StockParDepotResponse;
import ma.gestcom.modules.stock.dto.ValorisationResponse;
import org.springframework.data.domain.Pageable;

/**
 * Read-only stock reports.
 */
public interface StockQueryService {

    PageResponse<EtatStockResponse> etat(EtatStockFilter filter, Pageable pageable);

    PageResponse<EtatStockResponse> alertes(String search, Pageable pageable);

    ValorisationResponse valorisation();

    List<StockParDepotResponse> stockParDepot(Long articleId);

    PageResponse<MouvementResponse> mouvements(MouvementFilter filter, Pageable pageable);
}
