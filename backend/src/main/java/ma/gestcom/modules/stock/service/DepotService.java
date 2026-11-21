package ma.gestcom.modules.stock.service;

import java.util.List;
import java.util.Map;

import ma.gestcom.common.dto.PageResponse;
import ma.gestcom.modules.stock.dto.DepotRequest;
import ma.gestcom.modules.stock.dto.DepotResponse;
import org.springframework.data.domain.Pageable;

public interface DepotService {

    PageResponse<DepotResponse> search(String search, Boolean actif, Pageable pageable);

    List<DepotResponse> findAll();

    DepotResponse get(Long id);

    DepotResponse create(DepotRequest request);

    DepotResponse update(Long id, DepotRequest request);

    void delete(Long id);

    /** Validates that the depot exists and is active; returns it. */
    DepotResponse requireActif(Long id);

    Map<Long, String> getLibelles();
}
