package ma.gestcom.modules.utilisateur.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.gestcom.common.dto.PageResponse;
import ma.gestcom.modules.utilisateur.dto.ResetPasswordRequest;
import ma.gestcom.modules.utilisateur.dto.UtilisateurCreateRequest;
import ma.gestcom.modules.utilisateur.dto.UtilisateurFilter;
import ma.gestcom.modules.utilisateur.dto.UtilisateurResponse;
import ma.gestcom.modules.utilisateur.dto.UtilisateurUpdateRequest;
import ma.gestcom.modules.utilisateur.service.UtilisateurService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/utilisateurs")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UtilisateurController {

    private final UtilisateurService service;

    @GetMapping
    public PageResponse<UtilisateurResponse> search(@ParameterObject UtilisateurFilter filter,
                                                    @ParameterObject @PageableDefault(sort = "username",
                                                            direction = Sort.Direction.ASC) Pageable pageable) {
        return service.search(filter, pageable);
    }

    @GetMapping("/{id}")
    public UtilisateurResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UtilisateurResponse create(@Valid @RequestBody UtilisateurCreateRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    public UtilisateurResponse update(@PathVariable Long id, @Valid @RequestBody UtilisateurUpdateRequest request,
                                      Authentication authentication) {
        return service.update(id, request, authentication.getName());
    }

    @PutMapping("/{id}/mot-de-passe")
    public ResponseEntity<Void> resetPassword(@PathVariable Long id, @Valid @RequestBody ResetPasswordRequest request) {
        service.resetPassword(id, request.nouveauMotDePasse());
        return ResponseEntity.noContent().build();
    }
}
