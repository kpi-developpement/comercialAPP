package ma.gestcom.modules.tiers.repository;

import java.util.List;
import java.util.Optional;

import ma.gestcom.modules.tiers.entity.ClientArticleSuggere;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientArticleSuggereRepository extends JpaRepository<ClientArticleSuggere, Long> {

    List<ClientArticleSuggere> findByClientIdOrderByIdAsc(Long clientId);

    Optional<ClientArticleSuggere> findByIdAndClientId(Long id, Long clientId);

    boolean existsByClientIdAndArticleId(Long clientId, Long articleId);
}
