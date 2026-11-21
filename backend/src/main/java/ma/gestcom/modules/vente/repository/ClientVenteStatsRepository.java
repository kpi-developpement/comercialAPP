package ma.gestcom.modules.vente.repository;

import java.time.LocalDate;
import java.util.List;

import ma.gestcom.modules.vente.entity.DocumentVente;
import ma.gestcom.modules.vente.repository.projection.CaAnnuelRow;
import ma.gestcom.modules.vente.repository.projection.ProduitFactureRow;
import ma.gestcom.modules.vente.repository.projection.ProduitVenduRow;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

/**
 * Read-only sales statistics of one client (validated factures minus avoirs).
 */
public interface ClientVenteStatsRepository extends Repository<DocumentVente, Long> {

    String VALIDES = "('VALIDE','TRANSFORME_PARTIEL','TRANSFORME')";

    @Query(value = "select l.article_id as \"articleId\", l.article_code as code, max(l.designation) as designation, "
            + "sum(case when d.type = 'FACTURE' then l.quantite else -l.quantite end) as quantite, "
            + "sum(case when d.type = 'FACTURE' then l.montant_ht else -l.montant_ht end) as \"montantHt\", "
            + "count(distinct d.id) filter (where d.type = 'FACTURE') as \"nbFactures\", "
            + "max(d.date_document) as \"derniereVente\" "
            + "from document_vente_ligne l join document_vente d on d.id = l.document_id "
            + "where d.tiers_id = :clientId and d.type in ('FACTURE','AVOIR') and d.statut in " + VALIDES
            + " and l.article_id is not null "
            + "group by l.article_id, l.article_code order by \"montantHt\" desc", nativeQuery = true)
    List<ProduitVenduRow> produitsVendus(@Param("clientId") Long clientId);

    @Query(value = "select d.id as \"documentId\", d.type as type, d.numero as numero, d.date_document as \"dateDocument\", "
            + "l.prix_unitaire_ht as \"prixUnitaireHt\", l.quantite as quantite, l.montant_ht as \"montantHt\" "
            + "from document_vente_ligne l join document_vente d on d.id = l.document_id "
            + "where d.tiers_id = :clientId and l.article_id = :articleId and d.type in ('FACTURE','AVOIR') "
            + "and d.statut in " + VALIDES + " order by d.date_document desc, d.id desc", nativeQuery = true)
    List<ProduitFactureRow> facturesProduit(@Param("clientId") Long clientId, @Param("articleId") Long articleId);

    @Query(value = "select cast(extract(year from d.date_document) as integer) as annee, "
            + "sum(case when d.type = 'FACTURE' then d.total_ttc else -d.total_ttc end) as montant "
            + "from document_vente d where d.tiers_id = :clientId and d.type in ('FACTURE','AVOIR') "
            + "and d.statut in " + VALIDES + " and d.date_document between :debut and :fin "
            + "group by 1 order by 1", nativeQuery = true)
    List<CaAnnuelRow> caAnnuel(@Param("clientId") Long clientId, @Param("debut") LocalDate debut,
                               @Param("fin") LocalDate fin);
}
