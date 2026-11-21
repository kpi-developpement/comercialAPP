package ma.gestcom.modules.parametre.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.gestcom.modules.parametre.dto.SocieteRequest;
import ma.gestcom.modules.parametre.dto.SocieteResponse;
import ma.gestcom.modules.parametre.service.SocieteService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/parametres/societe")
@RequiredArgsConstructor
public class SocieteController {

    private final SocieteService service;

    @GetMapping
    public SocieteResponse get() {
        return service.get();
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public SocieteResponse update(@Valid @RequestBody SocieteRequest request) {
        return service.update(request);
    }
}
