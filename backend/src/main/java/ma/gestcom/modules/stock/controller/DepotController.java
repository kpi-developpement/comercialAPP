package ma.gestcom.modules.stock.controller;

import java.util.List;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.gestcom.common.dto.PageResponse;
import ma.gestcom.modules.stock.dto.DepotRequest;
import ma.gestcom.modules.stock.dto.DepotResponse;
import ma.gestcom.modules.stock.service.DepotService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/stock/depots")
@RequiredArgsConstructor
public class DepotController {

    private final DepotService service;

    @GetMapping
    public PageResponse<DepotResponse> search(@RequestParam(required = false) String search,
                                              @RequestParam(required = false) Boolean actif,
                                              @ParameterObject @PageableDefault(sort = "code") Pageable pageable) {
        return service.search(search, actif, pageable);
    }

    @GetMapping("/tous")
    public List<DepotResponse> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public DepotResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize(Roles.STOCK_WRITE)
    public DepotResponse create(@Valid @RequestBody DepotRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize(Roles.STOCK_WRITE)
    public DepotResponse update(@PathVariable Long id, @Valid @RequestBody DepotRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize(Roles.STOCK_WRITE)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
