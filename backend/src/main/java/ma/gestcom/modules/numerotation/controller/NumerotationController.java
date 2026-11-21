package ma.gestcom.modules.numerotation.controller;

import java.util.List;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.gestcom.common.enums.TypeDocument;
import ma.gestcom.modules.numerotation.dto.NumerotationConfigRequest;
import ma.gestcom.modules.numerotation.dto.NumerotationConfigResponse;
import ma.gestcom.modules.numerotation.service.NumerotationService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/parametres/numerotation")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class NumerotationController {

    private final NumerotationService service;

    @GetMapping
    public List<NumerotationConfigResponse> findAll() {
        return service.findAllConfigs();
    }

    @PutMapping("/{type}")
    public NumerotationConfigResponse update(@PathVariable TypeDocument type,
                                             @Valid @RequestBody NumerotationConfigRequest request) {
        return service.updateConfig(type, request);
    }
}
