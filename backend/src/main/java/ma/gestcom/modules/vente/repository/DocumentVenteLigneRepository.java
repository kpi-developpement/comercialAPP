package ma.gestcom.modules.vente.repository;

import ma.gestcom.modules.vente.entity.DocumentVenteLigne;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentVenteLigneRepository extends JpaRepository<DocumentVenteLigne, Long> {
}
