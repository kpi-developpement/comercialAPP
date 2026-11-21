package ma.gestcom.modules.dashboard.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import ma.gestcom.modules.dashboard.repository.projection.LabelValueRow;
import ma.gestcom.modules.vente.entity.DocumentVente;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

/**
 * Read-only aggregated queries of the dashboard (native SQL, no entity loading).
 * A sales document counts once validated (VALIDE, TRANSFORME_PARTIEL, TRANSFORME); avoirs are subtracted.
 */
public interface DashboardRepository extends Repository<DocumentVente, Long> {

    String VALIDES = "('VALIDE','TRANSFORME_PARTIEL','TRANSFORME')";

    @Query(value = "select coalesce(sum(case when d.type = 'FACTURE' then d.total_ht else -d.total_ht end), 0) "
            + "from document_vente d where d.type in ('FACTURE','AVOIR') and d.statut in " + VALIDES
            + " and d.date_document between :debut and :fin", nativeQuery = true)
    BigDecimal chiffreAffairesHt(@Param("debut") LocalDate debut, @Param("fin") LocalDate fin);

    @Query(value = "select coalesce(sum(case when d.type = 'FACTURE' then 1 else -1 end "
            + "* l.quantite * l.cout_unitaire), 0) "
            + "from document_vente_ligne l join document_vente d on d.id = l.document_id "
            + "where d.type in ('FACTURE','AVOIR') and d.statut in " + VALIDES
            + " and d.date_document between :debut and :fin", nativeQuery = true)
    BigDecimal coutDesVentes(@Param("debut") LocalDate debut, @Param("fin") LocalDate fin);

    @Query(value = "select count(*) from document_vente d where d.type = 'FACTURE' and d.statut in " + VALIDES
            + " and d.date_document between :debut and :fin", nativeQuery = true)
    long nombreFactures(@Param("debut") LocalDate debut, @Param("fin") LocalDate fin);

    @Query(value = "select coalesce(sum(d.total_ttc - d.montant_regle), 0) from document_vente d "
            + "where d.type = 'FACTURE' and d.statut in " + VALIDES, nativeQuery = true)
    BigDecimal totalImpayes();

    @Query(value = "select coalesce(sum(d.total_ttc - d.montant_regle), 0) from document_vente d "
            + "where d.type = 'FACTURE' and d.statut in " + VALIDES
            + " and d.date_echeance between :debut and :fin", nativeQuery = true)
    BigDecimal facturesAEcheance(@Param("debut") LocalDate debut, @Param("fin") LocalDate fin);

    @Query(value = "select coalesce(sum(r.montant), 0) from reglement r where r.sens = 'CLIENT' "
            + "and r.statut in ('EN_PORTEFEUILLE','REMIS_EN_BANQUE') "
            + "and r.date_echeance between :debut and :fin", nativeQuery = true)
    BigDecimal titresAEcheance(@Param("debut") LocalDate debut, @Param("fin") LocalDate fin);

    @Query(value = "select to_char(date_trunc('month', d.date_document), 'YYYY-MM') as label, "
            + "sum(case when d.type = 'FACTURE' then d.total_ht else -d.total_ht end) as valeur "
            + "from document_vente d where d.type in ('FACTURE','AVOIR') and d.statut in " + VALIDES
            + " and d.date_document between :debut and :fin group by 1 order by 1", nativeQuery = true)
    List<LabelValueRow> chiffreAffairesParMois(@Param("debut") LocalDate debut, @Param("fin") LocalDate fin);

    @Query(value = "select d.tiers_nom as label, "
            + "sum(case when d.type = 'FACTURE' then d.total_ht else -d.total_ht end) as valeur "
            + "from document_vente d where d.type in ('FACTURE','AVOIR') and d.statut in " + VALIDES
            + " and d.date_document between :debut and :fin "
            + "group by d.tiers_id, d.tiers_nom order by valeur desc limit 10", nativeQuery = true)
    List<LabelValueRow> topClients(@Param("debut") LocalDate debut, @Param("fin") LocalDate fin);

    @Query(value = "select coalesce(l.article_code || ' – ', '') || l.designation as label, "
            + "sum(case when d.type = 'FACTURE' then l.montant_ht else -l.montant_ht end) as valeur "
            + "from document_vente_ligne l join document_vente d on d.id = l.document_id "
            + "where d.type in ('FACTURE','AVOIR') and d.statut in " + VALIDES
            + " and d.date_document between :debut and :fin and l.article_id is not null "
            + "group by l.article_id, l.article_code, l.designation order by valeur desc limit 10", nativeQuery = true)
    List<LabelValueRow> topArticles(@Param("debut") LocalDate debut, @Param("fin") LocalDate fin);

    @Query(value = "select case "
            + " when current_date - coalesce(d.date_echeance, d.date_document) <= 30 then '0-30' "
            + " when current_date - coalesce(d.date_echeance, d.date_document) <= 60 then '31-60' "
            + " when current_date - coalesce(d.date_echeance, d.date_document) <= 90 then '61-90' "
            + " else '90+' end as label, sum(d.total_ttc - d.montant_regle) as valeur "
            + "from document_vente d where d.type = 'FACTURE' and d.statut in " + VALIDES
            + " and d.montant_regle < d.total_ttc group by 1", nativeQuery = true)
    List<LabelValueRow> impayesParAnciennete();
}
