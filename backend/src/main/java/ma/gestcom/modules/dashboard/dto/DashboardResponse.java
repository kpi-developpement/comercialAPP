package ma.gestcom.modules.dashboard.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * KPIs of the selected period and chart series.
 *
 * @param caParMois       last 12 months ending with the period end (months without sales are 0)
 * @param impayesParAge   buckets 0-30, 31-60, 61-90, 90+ (days past due date)
 */
public record DashboardResponse(
        LocalDate dateDebut,
        LocalDate dateFin,
        Kpis kpis,
        List<Point> caParMois,
        List<Point> topClients,
        List<Point> topArticles,
        List<Point> impayesParAge) {

    public record Kpis(
            BigDecimal chiffreAffairesHt,
            BigDecimal margeBrute,
            BigDecimal tauxMarge,
            long nombreFactures,
            BigDecimal totalImpayes,
            BigDecimal aEncaisserSemaine) {
    }

    public record Point(String label, BigDecimal valeur) {
    }
}
