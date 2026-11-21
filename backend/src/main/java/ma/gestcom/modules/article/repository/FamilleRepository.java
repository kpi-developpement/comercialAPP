package ma.gestcom.modules.article.repository;

import java.util.List;

import ma.gestcom.modules.article.entity.Famille;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FamilleRepository extends JpaRepository<Famille, Long>, JpaSpecificationExecutor<Famille> {

    @EntityGraph(attributePaths = "parent")
    List<Famille> findAllByOrderByCodeAsc();

    boolean existsByCodeIgnoreCase(String code);

    boolean existsByCodeIgnoreCaseAndIdNot(String code, Long id);

    boolean existsByParentId(Long parentId);
}
