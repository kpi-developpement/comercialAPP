package ma.gestcom.modules.parametre.mapper;

import ma.gestcom.modules.parametre.dto.ModeReglementRequest;
import ma.gestcom.modules.parametre.dto.ModeReglementResponse;
import ma.gestcom.modules.parametre.dto.SocieteRequest;
import ma.gestcom.modules.parametre.dto.SocieteResponse;
import ma.gestcom.modules.parametre.dto.TauxTvaRequest;
import ma.gestcom.modules.parametre.dto.TauxTvaResponse;
import ma.gestcom.modules.parametre.entity.ModeReglement;
import ma.gestcom.modules.parametre.entity.Societe;
import ma.gestcom.modules.parametre.entity.TauxTva;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper
public interface ParametreMapper {

    SocieteResponse toResponse(Societe entity);

    void update(@MappingTarget Societe entity, SocieteRequest request);

    TauxTvaResponse toResponse(TauxTva entity);

    TauxTva toEntity(TauxTvaRequest request);

    void update(@MappingTarget TauxTva entity, TauxTvaRequest request);

    ModeReglementResponse toResponse(ModeReglement entity);

    void update(@MappingTarget ModeReglement entity, ModeReglementRequest request);
}
