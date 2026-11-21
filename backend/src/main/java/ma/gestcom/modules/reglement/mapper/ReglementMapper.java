package ma.gestcom.modules.reglement.mapper;

import ma.gestcom.modules.reglement.dto.ReglementResponse;
import ma.gestcom.modules.reglement.entity.Reglement;
import ma.gestcom.modules.reglement.entity.ReglementAffectation;
import org.mapstruct.Mapper;

@Mapper
public interface ReglementMapper {

    ReglementResponse toResponse(Reglement entity);

    ReglementResponse.Affectation toAffectation(ReglementAffectation entity);
}
