package ma.gestcom.modules.dashboard.controller;

import java.time.LocalDate;

import lombok.RequiredArgsConstructor;
import ma.gestcom.modules.dashboard.dto.DashboardResponse;
import ma.gestcom.modules.dashboard.service.DashboardService;
import ma.gestcom.security.Roles;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService service;

    @GetMapping
    @PreAuthorize(Roles.DASHBOARD)
    public DashboardResponse get(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {
        return service.get(dateDebut, dateFin);
    }
}
