package ma.gestcom.modules.stock.repository;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.LockModeType;
import ma.gestcom.modules.stock.entity.StockArticleDepot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StockArticleDepotRepository extends JpaRepository<StockArticleDepot, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from StockArticleDepot s where s.articleId = :articleId and s.depotId = :depotId")
    Optional<StockArticleDepot> findForUpdate(@Param("articleId") Long articleId, @Param("depotId") Long depotId);

    Optional<StockArticleDepot> findByArticleIdAndDepotId(Long articleId, Long depotId);

    List<StockArticleDepot> findByArticleId(Long articleId);

    List<StockArticleDepot> findByDepotIdAndArticleIdIn(Long depotId, Collection<Long> articleIds);

    @Query("select coalesce(sum(s.quantite), 0) from StockArticleDepot s where s.articleId = :articleId")
    BigDecimal sumQuantite(@Param("articleId") Long articleId);

    @Modifying
    @Query(value = """
            insert into stock_article_depot (article_id, depot_id, quantite, created_at, updated_at, version)
            values (:articleId, :depotId, 0, now(), now(), 0)
            on conflict (article_id, depot_id) do nothing
            """, nativeQuery = true)
    int insertIfAbsent(@Param("articleId") Long articleId, @Param("depotId") Long depotId);
}
