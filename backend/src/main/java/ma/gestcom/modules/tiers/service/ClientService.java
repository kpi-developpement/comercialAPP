package ma.gestcom.modules.tiers.service;

import ma.gestcom.common.dto.PageResponse;
import ma.gestcom.modules.tiers.dto.ClientRequest;
import ma.gestcom.modules.tiers.dto.ClientResponse;
import ma.gestcom.modules.tiers.dto.TiersFilter;
import ma.gestcom.modules.tiers.dto.TiersInfo;
import ma.gestcom.modules.tiers.dto.TiersSummaryResponse;
import org.springframework.data.domain.Pageable;

public interface ClientService {

    PageResponse<TiersSummaryResponse> search(TiersFilter filter, Pageable pageable);

    ClientResponse get(Long id);

    ClientResponse create(ClientRequest request);

    ClientResponse update(Long id, ClientRequest request);

    void delete(Long id);

    TiersInfo getInfo(Long id);
}
