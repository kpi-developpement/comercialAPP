package ma.gestcom.modules.tiers.mapper;

import ma.gestcom.modules.tiers.dto.ClientRequest;
import ma.gestcom.modules.tiers.dto.ClientResponse;
import ma.gestcom.modules.tiers.dto.TiersInfo;
import ma.gestcom.modules.tiers.dto.TiersSummaryResponse;
import ma.gestcom.modules.tiers.entity.Client;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper
public interface ClientMapper {

    ClientResponse toResponse(Client entity);

    TiersSummaryResponse toSummary(Client entity);

    TiersInfo toInfo(Client entity);

    Client toEntity(ClientRequest request);

    void update(@MappingTarget Client entity, ClientRequest request);
}
