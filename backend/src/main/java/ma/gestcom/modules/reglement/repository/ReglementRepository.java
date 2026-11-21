package ma.gestcom.modules.reglement.repository;

import java.math.BigDecimal;

import ma.gestcom.common.enums.SensTiers;
import ma.gestcom.modules.reglement.entity.Reglement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReglementRepository extends JpaRepository<Reglement, Long>, JpaSpecificationExecutor<Reglement> {

    /** Advance payments not yet allocated to an invoice (unpaid instruments excluded). */
    @Query("select coalesce(sum(r.montant - r.montantAffecte), 0) from Reglement r "
            + "where r.sens = :sens and r.tiersId = :tiersId "
            + "and r.statut <> ma.gestcom.common.enums.StatutReglement.IMPAYE")
    BigDecimal sumNonAffecte(@Param("sens") SensTiers sens, @Param("tiersId") Long tiersId);
}
