package ma.gestcom.modules.stock.mapper;

import ma.gestcom.modules.stock.dto.TransfertResponse;
import ma.gestcom.modules.stock.entity.Transfert;
import ma.gestcom.modules.stock.entity.TransfertLigne;
import org.mapstruct.Mapper;

@Mapper
public interface TransfertMapper {

    TransfertResponse toResponse(Transfert entity, String depotSourceLibelle, String depotDestinationLibelle);

    TransfertResponse.Ligne toLigne(TransfertLigne ligne);
}
