package ma.gestcom.modules.stock.repository;

import ma.gestcom.modules.stock.entity.StockMouvement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface StockMouvementRepository extends JpaRepository<StockMouvement, Long>,
        JpaSpecificationExecutor<StockMouvement> {
}
