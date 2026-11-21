package ma.gestcom.modules.numerotation.mapper;

import ma.gestcom.modules.numerotation.dto.NumerotationConfigRequest;
import ma.gestcom.modules.numerotation.dto.NumerotationConfigResponse;
import ma.gestcom.modules.numerotation.entity.NumerotationConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper
public interface NumerotationMapper {

    @Mapping(target = "libelle", expression = "java(entity.getTypeDocument().getLibelle())")
    NumerotationConfigResponse toResponse(NumerotationConfig entity, String prochainNumero);

    void update(@MappingTarget NumerotationConfig entity, NumerotationConfigRequest request);
}
