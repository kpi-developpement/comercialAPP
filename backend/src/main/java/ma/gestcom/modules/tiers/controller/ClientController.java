package ma.gestcom.modules.tiers.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.gestcom.common.dto.PageResponse;
import ma.gestcom.modules.tiers.dto.ClientRequest;
import ma.gestcom.modules.tiers.dto.ClientResponse;
import ma.gestcom.modules.tiers.dto.TiersFilter;
import ma.gestcom.modules.tiers.dto.TiersSummaryResponse;
import ma.gestcom.modules.tiers.service.ClientService;
import ma.gestcom.security.Roles;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
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

@RestController
@RequestMapping("/api/v1/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService service;

    @GetMapping
    public PageResponse<TiersSummaryResponse> search(@ParameterObject TiersFilter filter,
                                                     @ParameterObject @PageableDefault(sort = "raisonSociale")
                                                     Pageable pageable) {
        return service.search(filter, pageable);
    }

    @GetMapping("/{id}")
    public ClientResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize(Roles.TIERS_WRITE)
    public ClientResponse create(@Valid @RequestBody ClientRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize(Roles.TIERS_WRITE)
    public ClientResponse update(@PathVariable Long id, @Valid @RequestBody ClientRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize(Roles.TIERS_WRITE)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
