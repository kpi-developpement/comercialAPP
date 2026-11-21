package ma.gestcom.modules.vente.repository;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import ma.gestcom.common.enums.StatutDocument;
import ma.gestcom.common.enums.TypeDocument;
import ma.gestcom.modules.document.dto.DocumentTypeRef;
import ma.gestcom.modules.vente.entity.DocumentVente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DocumentVenteRepository extends JpaRepository<DocumentVente, Long>,
        JpaSpecificationExecutor<DocumentVente> {

    Optional<DocumentVente> findByIdAndType(Long id, TypeDocument type);

    @Query("select new ma.gestcom.modules.document.dto.DocumentTypeRef(d.id, d.type) "
            + "from DocumentVente d where d.id in :ids")
    List<DocumentTypeRef> findTypes(@Param("ids") Collection<Long> ids);

    @Query("select coalesce(sum(d.totalTtc), 0) from DocumentVente d "
            + "where d.tiersId = :tiersId and d.type = :type and d.statut in :statuts")
    BigDecimal sumTtc(@Param("tiersId") Long tiersId, @Param("type") TypeDocument type,
                      @Param("statuts") Collection<StatutDocument> statuts);

    @Query("select coalesce(sum(d.montantRegle), 0) from DocumentVente d "
            + "where d.tiersId = :tiersId and d.type = :type and d.statut in :statuts")
    BigDecimal sumRegle(@Param("tiersId") Long tiersId, @Param("type") TypeDocument type,
                        @Param("statuts") Collection<StatutDocument> statuts);
}
