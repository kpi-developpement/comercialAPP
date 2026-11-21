package ma.gestcom.modules.dashboard.service;

import java.time.LocalDate;

import ma.gestcom.modules.dashboard.dto.DashboardResponse;

public interface DashboardService {

    /** Period defaults to the current year when a bound is missing. */
    DashboardResponse get(LocalDate dateDebut, LocalDate dateFin);
}
