package ma.gestcom.modules.stock.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.gestcom.common.dto.PageResponse;
import ma.gestcom.modules.stock.dto.OperationFilter;
import ma.gestcom.modules.stock.dto.TransfertRequest;
import ma.gestcom.modules.stock.dto.TransfertResponse;
import ma.gestcom.modules.stock.service.TransfertService;
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

@RestController
@RequestMapping("/api/v1/stock/transferts")
@RequiredArgsConstructor
public class TransfertController {

    private final TransfertService service;

    @GetMapping
    @PreAuthorize(Roles.STOCK_READ)
    public PageResponse<TransfertResponse> search(@ParameterObject OperationFilter filter,
                                                  @ParameterObject @PageableDefault(sort = "dateTransfert",
                                                          direction = Sort.Direction.DESC) Pageable pageable) {
        return service.search(filter, pageable);
    }

    @GetMapping("/{id}")
    @PreAuthorize(Roles.STOCK_READ)
    public TransfertResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize(Roles.STOCK_WRITE)
    public TransfertResponse create(@Valid @RequestBody TransfertRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize(Roles.STOCK_WRITE)
    public TransfertResponse update(@PathVariable Long id, @Valid @RequestBody TransfertRequest request) {
        return service.update(id, request);
    }

    @PostMapping("/{id}/valider")
    @PreAuthorize(Roles.STOCK_WRITE)
    public TransfertResponse valider(@PathVariable Long id) {
        return service.valider(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize(Roles.STOCK_WRITE)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
