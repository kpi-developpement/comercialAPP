package ma.gestcom.modules.tiers.controller;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import ma.gestcom.modules.tiers.dto.ArticleSuggereResponse;
import ma.gestcom.modules.tiers.service.ClientCatalogueService;
import ma.gestcom.security.Roles;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Suggested products catalogue of a client. */
@RestController
@RequestMapping("/api/v1/clients/{clientId}/suggestions")
@RequiredArgsConstructor
public class ClientCatalogueController {

    private final ClientCatalogueService service;

    public record AjoutSuggestionRequest(@NotNull Long articleId) {
    }

    @GetMapping
    public List<ArticleSuggereResponse> list(@PathVariable Long clientId) {
        return service.list(clientId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize(Roles.TIERS_WRITE)
    public ArticleSuggereResponse add(@PathVariable Long clientId, @Valid @RequestBody AjoutSuggestionRequest request) {
        return service.add(clientId, request.articleId());
    }

    @DeleteMapping("/{suggestionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize(Roles.TIERS_WRITE)
    public void remove(@PathVariable Long clientId, @PathVariable Long suggestionId) {
        service.remove(clientId, suggestionId);
    }
}
