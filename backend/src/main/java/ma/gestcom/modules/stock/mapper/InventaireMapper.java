package ma.gestcom.modules.stock.mapper;

import ma.gestcom.modules.stock.dto.InventaireResponse;
import ma.gestcom.modules.stock.dto.InventaireSummaryResponse;
import ma.gestcom.modules.stock.entity.Inventaire;
import ma.gestcom.modules.stock.entity.InventaireLigne;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface InventaireMapper {

    InventaireResponse toResponse(Inventaire entity, String depotLibelle);

    @Mapping(target = "nbLignes", expression = "java(entity.getLignes().size())")
    InventaireSummaryResponse toSummary(Inventaire entity, String depotLibelle);

    InventaireResponse.Ligne toLigne(InventaireLigne ligne);
}
