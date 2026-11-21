package ma.gestcom.modules.stock.mapper;

import ma.gestcom.modules.article.dto.ArticleInfo;
import ma.gestcom.modules.stock.dto.MouvementResponse;
import ma.gestcom.modules.stock.entity.StockMouvement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface MouvementMapper {

    @Mapping(target = "id", source = "mouvement.id")
    @Mapping(target = "type", source = "mouvement.type")
    @Mapping(target = "articleId", source = "mouvement.articleId")
    @Mapping(target = "articleCode", source = "article.code")
    @Mapping(target = "articleDesignation", source = "article.designation")
    @Mapping(target = "createdBy", source = "mouvement.createdBy")
    MouvementResponse toResponse(StockMouvement mouvement, ArticleInfo article, String depotLibelle);
}
