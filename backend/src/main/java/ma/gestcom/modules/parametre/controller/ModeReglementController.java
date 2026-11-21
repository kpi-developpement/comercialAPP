package ma.gestcom.modules.parametre.controller;

import java.util.List;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.gestcom.modules.parametre.dto.ModeReglementRequest;
import ma.gestcom.modules.parametre.dto.ModeReglementResponse;
import ma.gestcom.modules.parametre.service.ModeReglementService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/parametres/modes-reglement")
@RequiredArgsConstructor
public class ModeReglementController {

    private final ModeReglementService service;

    @GetMapping
    public List<ModeReglementResponse> findAll() {
        return service.findAll();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ModeReglementResponse update(@PathVariable Long id, @Valid @RequestBody ModeReglementRequest request) {
        return service.update(id, request);
    }
}
