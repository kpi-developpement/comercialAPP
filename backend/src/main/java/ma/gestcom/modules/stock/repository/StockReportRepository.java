package ma.gestcom.modules.stock.repository;

import java.util.List;

import ma.gestcom.modules.stock.entity.StockArticleDepot;
import ma.gestcom.modules.stock.repository.projection.EtatStockRow;
import ma.gestcom.modules.stock.repository.projection.ValorisationDepotRow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

/**
 * Read-only reporting queries (native SQL projections, no entity loading).
 * The inner select is wrapped so that Pageable sorting (code, designation, quantite, valeur) applies to aliases.
 */
public interface StockReportRepository extends Repository<StockArticleDepot, Long> {

    String ETAT_SELECT = """
            select * from (
                select a.id as "articleId", a.code as code, a.designation as designation,
                       f.libelle as "familleLibelle", a.unite as unite, a.stock_minimum as "stockMinimum",
                       a.pump as pump, a.famille_id as famille_id, f.parent_id as famille_parent_id,
                       coalesce(sum(s.quantite), 0) as quantite,
                       round(coalesce(sum(s.quantite), 0) * a.pump, 2) as valeur
                from article a
                left join famille f on f.id = a.famille_id
                left join stock_article_depot s on s.article_id = a.id
                     and (cast(:depotId as bigint) is null or s.depot_id = cast(:depotId as bigint))
                where a.supprime = false and a.type = 'STOCKE'
                group by a.id, f.libelle, f.parent_id
            ) t
            where (cast(:search as varchar) is null
                   or lower(t.code) like lower(concat('%', cast(:search as varchar), '%'))
                   or lower(t.designation) like lower(concat('%', cast(:search as varchar), '%')))
              and (cast(:familleId as bigint) is null or t.famille_id = cast(:familleId as bigint)
                   or t.famille_parent_id = cast(:familleId as bigint))
              and (cast(:sousMinimum as boolean) is null or cast(:sousMinimum as boolean) = false
                   or (t."stockMinimum" > 0 and t.quantite < t."stockMinimum"))
            """;

    @Query(value = ETAT_SELECT, countQuery = "select count(*) from (" + ETAT_SELECT + ") c", nativeQuery = true)
    Page<EtatStockRow> etatStock(@Param("search") String search, @Param("familleId") Long familleId,
                                 @Param("depotId") Long depotId, @Param("sousMinimum") Boolean sousMinimum,
                                 Pageable pageable);

    @Query(value = """
            select d.id as "depotId", d.code as "depotCode", d.libelle as "depotLibelle",
                   count(s.id) filter (where s.quantite <> 0) as "nbArticles",
                   coalesce(sum(s.quantite), 0) as quantite,
                   round(coalesce(sum(s.quantite * a.pump), 0), 2) as valeur
            from depot d
            left join stock_article_depot s on s.depot_id = d.id
            left join article a on a.id = s.article_id
            group by d.id, d.code, d.libelle
            order by d.code
            """, nativeQuery = true)
    List<ValorisationDepotRow> valorisationParDepot();
}
