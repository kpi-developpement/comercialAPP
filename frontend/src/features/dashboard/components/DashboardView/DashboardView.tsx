"use client";

import { useState } from "react";
import { PageHeader } from "@/components/layout/PageHeader";
import { Card } from "@/components/ui/Card";
import { ErrorState } from "@/components/ui/ErrorState";
import { Skeleton } from "@/components/ui/Skeleton";
import { RequireAuth } from "@/features/auth/components/RequireAuth";
import { formatDate, formatMoney, formatPercent } from "@/lib/format";
import { useDashboard } from "../../hooks/useDashboard";
import { AgingChart } from "../AgingChart";
import { KpiCard } from "../KpiCard";
import { PeriodSelector, PRESETS, type Period } from "../PeriodSelector";
import { RevenueChart } from "../RevenueChart";
import { TopArticlesChart } from "../TopArticlesChart";
import { TopClientsChart } from "../TopClientsChart";
import styles from "./DashboardView.module.css";

const money = (n: number) => formatMoney(n);
const count = (n: number) => String(Math.round(n));

export function DashboardView() {
  const [preset, setPreset] = useState("annee");
  const [period, setPeriod] = useState<Period>(PRESETS.annee!());
  const query = useDashboard(period.dateDebut, period.dateFin);
  const d = query.data;

  return (
    <RequireAuth permission="dashboard">
      <PageHeader
        title="Tableau de bord"
        subtitle={d ? `Du ${formatDate(d.dateDebut)} au ${formatDate(d.dateFin)}` : undefined}
        actions={<PeriodSelector preset={preset} period={period} onChange={(p, per) => { setPreset(p); setPeriod(per); }} />}
      />
      {query.error ? (
        <ErrorState error={query.error} onRetry={() => query.refetch()} />
      ) : (
        <>
          <div className={styles.kpis}>
            {!d ? Array.from({ length: 5 }).map((_, i) => <Skeleton key={i} height={96} />) : (
              <>
                <KpiCard index={0} label="Chiffre d'affaires HT" value={Number(d.kpis.chiffreAffairesHt)} format={money} hint="Factures − avoirs validés" />
                <KpiCard index={1} label="Marge brute" value={Number(d.kpis.margeBrute)} format={money}
                  hint={`Taux de marge ${formatPercent(d.kpis.tauxMarge)} (coût au PUMP)`}
                  tone={Number(d.kpis.margeBrute) < 0 ? "danger" : "success"} />
                <KpiCard index={2} label="Factures émises" value={d.kpis.nombreFactures} format={count} hint="Sur la période" />
                <KpiCard index={3} label="Total impayés" value={Number(d.kpis.totalImpayes)} format={money} hint="Reste à payer, toutes factures"
                  tone={Number(d.kpis.totalImpayes) > 0 ? "danger" : "default"} />
                <KpiCard index={4} label="À encaisser cette semaine" value={Number(d.kpis.aEncaisserSemaine)} format={money}
                  hint="Échéances factures + chèques / effets" />
              </>
            )}
          </div>
          <div className={styles.grid}>
            <Card index={0} title="Chiffre d'affaires HT – 12 mois" className={styles.wide}>
              {d ? <RevenueChart data={d.caParMois} /> : <Skeleton height={240} />}
            </Card>
            <Card index={1} title="Top 10 clients">{d ? <TopClientsChart data={d.topClients} /> : <Skeleton height={240} />}</Card>
            <Card index={2} title="Top 10 articles">{d ? <TopArticlesChart data={d.topArticles} /> : <Skeleton height={240} />}</Card>
            <Card index={3} title="Impayés par ancienneté" className={styles.wide}>{d ? <AgingChart data={d.impayesParAge} /> : <Skeleton height={140} />}</Card>
          </div>
        </>
      )}
    </RequireAuth>
  );
}
