package ma.gestcom.modules.tiers.service;

import ma.gestcom.common.dto.PageResponse;
import ma.gestcom.modules.tiers.dto.FournisseurRequest;
import ma.gestcom.modules.tiers.dto.FournisseurResponse;
import ma.gestcom.modules.tiers.dto.TiersFilter;
import ma.gestcom.modules.tiers.dto.TiersInfo;
import ma.gestcom.modules.tiers.dto.TiersSummaryResponse;
import org.springframework.data.domain.Pageable;

public interface FournisseurService {

    PageResponse<TiersSummaryResponse> search(TiersFilter filter, Pageable pageable);

    FournisseurResponse get(Long id);

    FournisseurResponse create(FournisseurRequest request);

    FournisseurResponse update(Long id, FournisseurRequest request);

    void delete(Long id);

    TiersInfo getInfo(Long id);
}
