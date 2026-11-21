package ma.gestcom.modules.numerotation.service;

import java.time.LocalDate;
import java.util.List;

import ma.gestcom.common.enums.TypeDocument;
import ma.gestcom.modules.numerotation.dto.NumerotationConfigRequest;
import ma.gestcom.modules.numerotation.dto.NumerotationConfigResponse;

public interface NumerotationService {

    /**
     * Reserves the next number of the given type for the year of {@code date}. Must be called inside the
     * transaction that validates the document so that a rollback also releases the number (no gaps).
     */
    String nextNumero(TypeDocument type, LocalDate date);

    List<NumerotationConfigResponse> findAllConfigs();

    NumerotationConfigResponse updateConfig(TypeDocument type, NumerotationConfigRequest request);
}
