package ma.gestcom.modules.article.mapper;

import ma.gestcom.modules.article.dto.FamilleRequest;
import ma.gestcom.modules.article.dto.FamilleResponse;
import ma.gestcom.modules.article.entity.Famille;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper
public interface FamilleMapper {

    @Mapping(target = "parentId", source = "parent.id")
    @Mapping(target = "parentLibelle", source = "parent.libelle")
    FamilleResponse toResponse(Famille entity);

    @Mapping(target = "parent", ignore = true)
    Famille toEntity(FamilleRequest request);

    @Mapping(target = "parent", ignore = true)
    void update(@MappingTarget Famille entity, FamilleRequest request);
}
