package ma.gestcom.modules.article.service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import ma.gestcom.common.dto.PageResponse;
import ma.gestcom.modules.article.dto.ArticleFilter;
import ma.gestcom.modules.article.dto.ArticleInfo;
import ma.gestcom.modules.article.dto.ArticleRequest;
import ma.gestcom.modules.article.dto.ArticleResponse;
import ma.gestcom.modules.article.dto.ArticleSummaryResponse;
import org.springframework.data.domain.Pageable;

public interface ArticleService {

    PageResponse<ArticleSummaryResponse> search(ArticleFilter filter, Pageable pageable);

    ArticleResponse get(Long id);

    /** Exact lookup by barcode, then by code (used by the barcode scanner of the lines grid). */
    ArticleResponse findByScan(String value);

    ArticleResponse create(ArticleRequest request);

    ArticleResponse update(Long id, ArticleRequest request);

    void delete(Long id);

    // --- API for other modules ---

    ArticleInfo getInfo(Long id);

    Map<Long, ArticleInfo> getInfos(Collection<Long> ids);

    /** Active stocked articles, optionally restricted to a family and its sous-familles (inventory sheets). */
    List<ArticleInfo> findStockables(Long familleId);

    /** Only the stock module may call this, after an entry movement. */
    void updatePump(Long id, BigDecimal pump);
}
