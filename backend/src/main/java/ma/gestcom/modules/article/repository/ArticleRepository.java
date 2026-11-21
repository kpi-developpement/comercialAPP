package ma.gestcom.modules.article.repository;

import java.util.Optional;

import ma.gestcom.modules.article.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ArticleRepository extends JpaRepository<Article, Long>, JpaSpecificationExecutor<Article> {

    Optional<Article> findByIdAndSupprimeFalse(Long id);

    Optional<Article> findByCodeBarresAndSupprimeFalse(String codeBarres);

    Optional<Article> findByCodeIgnoreCaseAndSupprimeFalse(String code);

    boolean existsByCodeIgnoreCase(String code);

    boolean existsByCodeIgnoreCaseAndIdNot(String code, Long id);

    boolean existsByCodeBarres(String codeBarres);

    boolean existsByCodeBarresAndIdNot(String codeBarres, Long id);

    boolean existsByFamilleIdAndSupprimeFalse(Long familleId);
}
