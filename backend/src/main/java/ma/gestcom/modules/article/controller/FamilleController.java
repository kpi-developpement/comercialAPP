package ma.gestcom.modules.article.controller;

import java.util.List;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.gestcom.common.dto.PageResponse;
import ma.gestcom.modules.article.dto.FamilleFilter;
import ma.gestcom.modules.article.dto.FamilleRequest;
import ma.gestcom.modules.article.dto.FamilleResponse;
import ma.gestcom.modules.article.service.FamilleService;
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
@RequestMapping("/api/v1/familles")
@RequiredArgsConstructor
public class FamilleController {

    private final FamilleService service;

    @GetMapping
    public PageResponse<FamilleResponse> search(@ParameterObject FamilleFilter filter,
                                                @ParameterObject @PageableDefault(sort = "code") Pageable pageable) {
        return service.search(filter, pageable);
    }

    @GetMapping("/toutes")
    public List<FamilleResponse> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public FamilleResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize(Roles.ARTICLE_WRITE)
    public FamilleResponse create(@Valid @RequestBody FamilleRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize(Roles.ARTICLE_WRITE)
    public FamilleResponse update(@PathVariable Long id, @Valid @RequestBody FamilleRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize(Roles.ARTICLE_WRITE)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
