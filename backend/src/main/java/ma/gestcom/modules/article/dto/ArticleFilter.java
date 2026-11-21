package ma.gestcom.modules.article.dto;

import ma.gestcom.common.enums.ArticleType;

/**
 * @param search matches code, désignation or code-barres
 */
public record ArticleFilter(String search, Long familleId, ArticleType type, Boolean actif) {
}
