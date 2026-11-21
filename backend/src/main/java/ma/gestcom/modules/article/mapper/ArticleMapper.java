package ma.gestcom.modules.article.mapper;

import ma.gestcom.modules.article.dto.ArticleInfo;
import ma.gestcom.modules.article.dto.ArticleRequest;
import ma.gestcom.modules.article.dto.ArticleResponse;
import ma.gestcom.modules.article.dto.ArticleSummaryResponse;
import ma.gestcom.modules.article.entity.Article;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper
public interface ArticleMapper {

    @Mapping(target = "familleId", source = "famille.id")
    @Mapping(target = "familleLibelle", source = "famille.libelle")
    ArticleResponse toResponse(Article entity);

    @Mapping(target = "familleLibelle", source = "famille.libelle")
    ArticleSummaryResponse toSummary(Article entity);

    ArticleInfo toInfo(Article entity);

    @Mapping(target = "famille", ignore = true)
    @Mapping(target = "pump", ignore = true)
    Article toEntity(ArticleRequest request);

    @Mapping(target = "famille", ignore = true)
    @Mapping(target = "pump", ignore = true)
    void update(@MappingTarget Article entity, ArticleRequest request);
}
