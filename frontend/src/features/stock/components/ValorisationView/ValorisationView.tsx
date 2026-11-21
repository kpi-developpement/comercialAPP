"use client";

import { PageHeader } from "@/components/layout/PageHeader";
import { Card } from "@/components/ui/Card";
import { Table, type Column } from "@/components/ui/Table";
import { formatMoney, formatQty } from "@/lib/format";
import { useValorisation } from "../../hooks/useStockReports";
import type { Valorisation } from "../../types/stock.types";
import styles from "./ValorisationView.module.css";

type Row = Valorisation["depots"][number];

const COLUMNS: Column<Row>[] = [
  { key: "code", header: "Code", width: "120px", render: (r) => <strong>{r.depotCode}</strong> },
  { key: "libelle", header: "Dépôt", render: (r) => r.depotLibelle },
  { key: "nb", header: "Articles en stock", align: "right", render: (r) => r.nbArticles },
  { key: "qte", header: "Quantité totale", align: "right", render: (r) => formatQty(r.quantite) },
  { key: "valeur", header: "Valeur au PUMP", align: "right", render: (r) => formatMoney(r.valeur) },
];

export function ValorisationView() {
  const query = useValorisation();
  return (
    <>
      <PageHeader title="Valorisation du stock" subtitle="Quantité × PUMP, par dépôt" />
      {query.data && (
        <div className={styles.total}>
          <span>Valeur totale du stock</span>
          <strong>{formatMoney(query.data.valeurTotale, "MAD")}</strong>
        </div>
      )}
      <Card padded={false}>
        <Table
          columns={COLUMNS}
          rows={query.data?.depots}
          rowKey={(r) => r.depotId}
          loading={query.isLoading}
          error={query.error}
          onRetry={() => query.refetch()}
        />
      </Card>
    </>
  );
}
