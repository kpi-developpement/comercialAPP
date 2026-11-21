package ma.gestcom.modules.stock.repository;

import ma.gestcom.modules.stock.entity.Inventaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface InventaireRepository extends JpaRepository<Inventaire, Long>, JpaSpecificationExecutor<Inventaire> {
}
