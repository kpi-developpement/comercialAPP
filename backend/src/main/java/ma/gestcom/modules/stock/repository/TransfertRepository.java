package ma.gestcom.modules.stock.repository;

import ma.gestcom.modules.stock.entity.Transfert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TransfertRepository extends JpaRepository<Transfert, Long>, JpaSpecificationExecutor<Transfert> {
}
