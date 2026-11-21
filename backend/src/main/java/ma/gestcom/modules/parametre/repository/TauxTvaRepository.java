package ma.gestcom.modules.parametre.repository;

import java.math.BigDecimal;
import java.util.List;

import ma.gestcom.modules.parametre.entity.TauxTva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface TauxTvaRepository extends JpaRepository<TauxTva, Long> {

    List<TauxTva> findAllByOrderByTauxDesc();

    boolean existsByTaux(BigDecimal taux);

    boolean existsByTauxAndIdNot(BigDecimal taux, Long id);

    @Modifying
    @Query("update TauxTva t set t.parDefaut = false where t.id <> :id")
    void clearDefaultExcept(Long id);
}
