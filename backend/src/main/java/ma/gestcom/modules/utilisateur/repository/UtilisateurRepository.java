package ma.gestcom.modules.utilisateur.repository;

import java.util.Optional;

import ma.gestcom.common.enums.Role;
import ma.gestcom.modules.utilisateur.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long>, JpaSpecificationExecutor<Utilisateur> {

    Optional<Utilisateur> findByUsernameIgnoreCase(String username);

    boolean existsByUsernameIgnoreCase(String username);

    boolean existsByRoleAndActifTrue(Role role);
}
