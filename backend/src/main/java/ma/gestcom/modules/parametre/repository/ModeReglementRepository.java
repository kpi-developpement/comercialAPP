package ma.gestcom.modules.parametre.repository;

import java.util.List;

import ma.gestcom.modules.parametre.entity.ModeReglement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModeReglementRepository extends JpaRepository<ModeReglement, Long> {

    List<ModeReglement> findAllByOrderByIdAsc();
}
