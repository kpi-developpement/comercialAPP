package ma.gestcom.modules.stock.repository;

import java.util.List;
import java.util.Optional;

import ma.gestcom.modules.stock.entity.Depot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface DepotRepository extends JpaRepository<Depot, Long>, JpaSpecificationExecutor<Depot> {

    List<Depot> findAllByOrderByCodeAsc();

    Optional<Depot> findFirstByParDefautTrueAndActifTrue();

    boolean existsByCodeIgnoreCase(String code);

    boolean existsByCodeIgnoreCaseAndIdNot(String code, Long id);

    @Modifying
    @Query("update Depot d set d.parDefaut = false where d.id <> :id")
    void clearDefaultExcept(Long id);
}
