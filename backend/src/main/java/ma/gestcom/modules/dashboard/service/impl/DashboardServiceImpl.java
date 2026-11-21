package ma.gestcom.modules.dashboard.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import ma.gestcom.common.util.MoneyUtil;
import ma.gestcom.modules.dashboard.dto.DashboardResponse;
import ma.gestcom.modules.dashboard.repository.DashboardRepository;
import ma.gestcom.modules.dashboard.repository.projection.LabelValueRow;
import ma.gestcom.modules.dashboard.service.DashboardService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private static final List<String> TRANCHES = List.of("0-30", "31-60", "61-90", "90+");

    private final DashboardRepository repository;

    @Override
    public DashboardResponse get(LocalDate dateDebut, LocalDate dateFin) {
        LocalDate fin = dateFin != null ? dateFin : LocalDate.now().withDayOfYear(LocalDate.now().lengthOfYear());
        LocalDate debut = dateDebut != null ? dateDebut : fin.withDayOfYear(1);

        BigDecimal ca = MoneyUtil.money(repository.chiffreAffairesHt(debut, fin));
        BigDecimal marge = MoneyUtil.money(ca.subtract(MoneyUtil.nz(repository.coutDesVentes(debut, fin))));
        BigDecimal taux = ca.signum() == 0 ? BigDecimal.ZERO
                : marge.multiply(MoneyUtil.HUNDRED).divide(ca, 2, RoundingMode.HALF_UP);
        LocalDate lundi = LocalDate.now().with(DayOfWeek.MONDAY);
        LocalDate dimanche = lundi.plusDays(6);
        BigDecimal semaine = MoneyUtil.money(repository.facturesAEcheance(lundi, dimanche)
                .add(repository.titresAEcheance(lundi, dimanche)));

        DashboardResponse.Kpis kpis = new DashboardResponse.Kpis(ca, marge, taux,
                repository.nombreFactures(debut, fin), MoneyUtil.money(repository.totalImpayes()), semaine);
        return new DashboardResponse(debut, fin, kpis, caParMois(fin),
                points(repository.topClients(debut, fin)),
                points(repository.topArticles(debut, fin)),
                impayesParAge());
    }

    /** 12 consecutive months ending with the month of {@code fin}, zero-filled. */
    private List<DashboardResponse.Point> caParMois(LocalDate fin) {
        YearMonth dernier = YearMonth.from(fin);
        YearMonth premier = dernier.minusMonths(11);
        Map<String, BigDecimal> valeurs = repository
                .chiffreAffairesParMois(premier.atDay(1), dernier.atEndOfMonth()).stream()
                .collect(Collectors.toMap(LabelValueRow::getLabel, LabelValueRow::getValeur));
        List<DashboardResponse.Point> points = new ArrayList<>();
        for (YearMonth m = premier; !m.isAfter(dernier); m = m.plusMonths(1)) {
            String label = m.toString();
            points.add(new DashboardResponse.Point(label, MoneyUtil.money(valeurs.get(label))));
        }
        return points;
    }

    private List<DashboardResponse.Point> impayesParAge() {
        Map<String, BigDecimal> valeurs = repository.impayesParAnciennete().stream()
                .collect(Collectors.toMap(LabelValueRow::getLabel, LabelValueRow::getValeur));
        return TRANCHES.stream()
                .map(t -> new DashboardResponse.Point(t, MoneyUtil.money(valeurs.get(t))))
                .toList();
    }

    private static List<DashboardResponse.Point> points(List<LabelValueRow> rows) {
        return rows.stream().map(r -> new DashboardResponse.Point(r.getLabel(), MoneyUtil.money(r.getValeur()))).toList();
    }
}
