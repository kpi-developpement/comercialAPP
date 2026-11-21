"use client";

import { useRouter } from "next/navigation";
import type { ReactNode } from "react";
import { Badge } from "@/components/ui/Badge";
import { Table, type Column } from "@/components/ui/Table";
import { formatMoney, formatQty } from "@/lib/format";
import type { EtatStock } from "../../types/stock.types";

interface EtatStockTableProps {
  rows: EtatStock[] | undefined;
  loading: boolean;
  error: unknown;
  onRetry: () => void;
  sort?: string;
  onSort: (field: string) => void;
  footer: ReactNode;
  empty?: ReactNode;
}

const COLUMNS: Column<EtatStock>[] = [
  { key: "code", header: "Code", sortKey: "code", width: "120px", render: (r) => <strong>{r.code}</strong> },
  { key: "designation", header: "Désignation", sortKey: "designation", render: (r) => r.designation },
  { key: "famille", header: "Famille", render: (r) => r.familleLibelle ?? "—" },
  { key: "quantite", header: "Quantité", sortKey: "quantite", align: "right", render: (r) => `${formatQty(r.quantite)} ${r.unite}` },
  { key: "min", header: "Stock min.", align: "right", render: (r) => formatQty(r.stockMinimum) },
  { key: "pump", header: "PUMP", align: "right", render: (r) => formatMoney(r.pump) },
  { key: "valeur", header: "Valeur", sortKey: "valeur", align: "right", render: (r) => formatMoney(r.valeur) },
  {
    key: "alerte",
    header: "Alerte",
    render: (r) => (r.sousMinimum ? <Badge tone="danger">Sous le minimum</Badge> : Number(r.quantite) < 0 ? <Badge tone="warning">Négatif</Badge> : null),
  },
];

export function EtatStockTable({ rows, loading, error, onRetry, sort, onSort, footer, empty }: EtatStockTableProps) {
  const router = useRouter();
  return (
    <Table
      columns={COLUMNS}
      rows={rows}
      rowKey={(r) => r.articleId}
      loading={loading}
      error={error}
      onRetry={onRetry}
      sort={sort}
      onSort={onSort}
      onRowClick={(r) => router.push(`/articles/${r.articleId}`)}
      footer={footer}
      empty={empty}
    />
  );
}
