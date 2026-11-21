package ma.gestcom.modules.stock.mapper;

import ma.gestcom.modules.stock.dto.DepotRequest;
import ma.gestcom.modules.stock.dto.DepotResponse;
import ma.gestcom.modules.stock.entity.Depot;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper
public interface DepotMapper {

    DepotResponse toResponse(Depot entity);

    Depot toEntity(DepotRequest request);

    void update(@MappingTarget Depot entity, DepotRequest request);
}
