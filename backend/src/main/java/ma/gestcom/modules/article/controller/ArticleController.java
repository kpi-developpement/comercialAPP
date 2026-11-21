package ma.gestcom.modules.article.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.gestcom.common.dto.PageResponse;
import ma.gestcom.modules.article.dto.ArticleFilter;
import ma.gestcom.modules.article.dto.ArticleRequest;
import ma.gestcom.modules.article.dto.ArticleResponse;
import ma.gestcom.modules.article.dto.ArticleSummaryResponse;
import ma.gestcom.modules.article.service.ArticleService;
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
@RequestMapping("/api/v1/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService service;

    @GetMapping
    public PageResponse<ArticleSummaryResponse> search(@ParameterObject ArticleFilter filter,
                                                       @ParameterObject @PageableDefault(sort = "code")
                                                       Pageable pageable) {
        return service.search(filter, pageable);
    }

    @GetMapping("/scan")
    public ArticleResponse scan(@RequestParam String valeur) {
        return service.findByScan(valeur);
    }

    @GetMapping("/{id}")
    public ArticleResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize(Roles.ARTICLE_WRITE)
    public ArticleResponse create(@Valid @RequestBody ArticleRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize(Roles.ARTICLE_WRITE)
    public ArticleResponse update(@PathVariable Long id, @Valid @RequestBody ArticleRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize(Roles.ARTICLE_WRITE)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
