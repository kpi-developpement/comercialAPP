package ma.gestcom.modules.stock.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;
import ma.gestcom.common.dto.PageResponse;
import ma.gestcom.modules.stock.dto.EtatStockFilter;
import ma.gestcom.modules.stock.dto.EtatStockResponse;
import ma.gestcom.modules.stock.dto.MouvementFilter;
import ma.gestcom.modules.stock.dto.MouvementResponse;
import ma.gestcom.modules.stock.dto.StockParDepotResponse;
import ma.gestcom.modules.stock.dto.ValorisationResponse;
import ma.gestcom.modules.stock.service.StockQueryService;
import ma.gestcom.security.Roles;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Read-only stock reports: état, alertes, valorisation, mouvements.
 */
@RestController
@RequestMapping("/api/v1/stock")
@RequiredArgsConstructor
@PreAuthorize(Roles.STOCK_READ)
public class StockController {

    private final StockQueryService service;

    @GetMapping("/etat")
    public PageResponse<EtatStockResponse> etat(@ParameterObject EtatStockFilter filter,
                                                @ParameterObject @PageableDefault(sort = "code") Pageable pageable) {
        return service.etat(filter, pageable);
    }

    @GetMapping("/alertes")
    public PageResponse<EtatStockResponse> alertes(@RequestParam(required = false) String search,
                                                   @ParameterObject @PageableDefault(sort = "code")
                                                   Pageable pageable) {
        return service.alertes(search, pageable);
    }

    @GetMapping("/valorisation")
    public ValorisationResponse valorisation() {
        return service.valorisation();
    }

    @GetMapping("/articles/{articleId}")
    public List<StockParDepotResponse> stockParDepot(@PathVariable Long articleId) {
        return service.stockParDepot(articleId);
    }

    @GetMapping("/mouvements")
    public PageResponse<MouvementResponse> mouvements(@ParameterObject MouvementFilter filter,
                                                      @ParameterObject @PageableDefault(sort = "dateMouvement",
                                                              direction = Sort.Direction.DESC) Pageable pageable) {
        return service.mouvements(filter, pageable);
    }
}
