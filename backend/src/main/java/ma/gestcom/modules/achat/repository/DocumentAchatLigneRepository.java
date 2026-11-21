package ma.gestcom.modules.achat.repository;

import ma.gestcom.modules.achat.entity.DocumentAchatLigne;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentAchatLigneRepository extends JpaRepository<DocumentAchatLigne, Long> {
}
