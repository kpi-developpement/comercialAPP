package ma.gestcom.modules.tiers.repository;

import java.util.Optional;

import ma.gestcom.modules.tiers.entity.Fournisseur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FournisseurRepository extends JpaRepository<Fournisseur, Long>, JpaSpecificationExecutor<Fournisseur> {

    Optional<Fournisseur> findByIdAndSupprimeFalse(Long id);

    boolean existsByCodeIgnoreCase(String code);

    boolean existsByCodeIgnoreCaseAndIdNot(String code, Long id);
}
