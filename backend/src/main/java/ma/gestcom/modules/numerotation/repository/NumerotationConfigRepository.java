package ma.gestcom.modules.numerotation.repository;

import java.util.List;
import java.util.Optional;

import ma.gestcom.common.enums.TypeDocument;
import ma.gestcom.modules.numerotation.entity.NumerotationConfig;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NumerotationConfigRepository extends JpaRepository<NumerotationConfig, Long> {

    Optional<NumerotationConfig> findByTypeDocument(TypeDocument typeDocument);

    List<NumerotationConfig> findAllByOrderByIdAsc();
}
