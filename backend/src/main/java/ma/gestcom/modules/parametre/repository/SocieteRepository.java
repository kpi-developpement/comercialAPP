package ma.gestcom.modules.parametre.repository;

import java.util.Optional;

import ma.gestcom.modules.parametre.entity.Societe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SocieteRepository extends JpaRepository<Societe, Long> {

    Optional<Societe> findFirstByOrderByIdAsc();
}
