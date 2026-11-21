package ma.gestcom.modules.reglement.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.gestcom.common.dto.PageResponse;
import ma.gestcom.common.enums.SensTiers;
import ma.gestcom.modules.document.dto.DocumentSummaryResponse;
import ma.gestcom.modules.reglement.dto.AffectationsRequest;
import ma.gestcom.modules.reglement.dto.EcheancierFilter;
import ma.gestcom.modules.reglement.dto.ReglementFilter;
import ma.gestcom.modules.reglement.dto.ReglementRequest;
import ma.gestcom.modules.reglement.dto.ReglementResponse;
import ma.gestcom.modules.reglement.dto.SoldeTiersResponse;
import ma.gestcom.modules.reglement.dto.StatutReglementRequest;
import ma.gestcom.modules.reglement.service.ReglementService;
import ma.gestcom.modules.reglement.service.SoldeService;
import ma.gestcom.security.Roles;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Payments. {@code sens} is "clients" or "fournisseurs".
 */
@RestController
@RequestMapping("/api/v1/reglements/{sens}")
@RequiredArgsConstructor
public class ReglementController {

    private final ReglementService service;
    private final SoldeService soldeService;

    @GetMapping
    @PreAuthorize(Roles.REGLEMENT_READ)
    public PageResponse<ReglementResponse> search(@PathVariable String sens, @ParameterObject ReglementFilter filter,
                                                  @ParameterObject @PageableDefault(sort = "dateReglement",
                                                          direction = Sort.Direction.DESC) Pageable pageable) {
        return service.search(sensOf(sens), filter, pageable);
    }

    @GetMapping("/{id}")
    @PreAuthorize(Roles.REGLEMENT_READ)
    public ReglementResponse get(@PathVariable String sens, @PathVariable Long id) {
        return service.get(sensOf(sens), id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize(Roles.REGLEMENT_WRITE)
    public ReglementResponse create(@PathVariable String sens, @Valid @RequestBody ReglementRequest request) {
        return service.create(sensOf(sens), request);
    }

    @PostMapping("/{id}/affectations")
    @PreAuthorize(Roles.REGLEMENT_WRITE)
    public ReglementResponse affecter(@PathVariable String sens, @PathVariable Long id,
                                      @Valid @RequestBody AffectationsRequest request) {
        return service.affecter(sensOf(sens), id, request);
    }

    @PutMapping("/{id}/statut")
    @PreAuthorize(Roles.REGLEMENT_WRITE)
    public ReglementResponse changerStatut(@PathVariable String sens, @PathVariable Long id,
                                           @Valid @RequestBody StatutReglementRequest request) {
        return service.changerStatut(sensOf(sens), id, request.statut());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize(Roles.REGLEMENT_WRITE)
    public void delete(@PathVariable String sens, @PathVariable Long id) {
        service.delete(sensOf(sens), id);
    }

    @GetMapping("/soldes/{tiersId}")
    @PreAuthorize(Roles.REGLEMENT_READ)
    public SoldeTiersResponse solde(@PathVariable String sens, @PathVariable Long tiersId) {
        return soldeService.solde(sensOf(sens), tiersId);
    }

    @GetMapping("/echeancier")
    @PreAuthorize(Roles.REGLEMENT_READ)
    public PageResponse<DocumentSummaryResponse> echeancier(@PathVariable String sens,
                                                            @ParameterObject EcheancierFilter filter,
                                                            @ParameterObject @PageableDefault(sort = "dateEcheance")
                                                            Pageable pageable) {
        return soldeService.echeancier(sensOf(sens), filter, pageable);
    }

    private static SensTiers sensOf(String path) {
        return switch (path) {
            case "clients" -> SensTiers.CLIENT;
            case "fournisseurs" -> SensTiers.FOURNISSEUR;
            default -> throw new IllegalArgumentException("Sens de règlement inconnu : " + path);
        };
    }
}
