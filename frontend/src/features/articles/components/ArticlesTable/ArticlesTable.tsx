"use client";

import { useRouter } from "next/navigation";
import type { ReactNode } from "react";
import { Badge } from "@/components/ui/Badge";
import { Table, type Column } from "@/components/ui/Table";
import { formatMoney, formatPercent } from "@/lib/format";
import type { ArticleSummary } from "../../types/article.types";
import { ArticleTypeBadge } from "../ArticleTypeBadge";
import styles from "./ArticlesTable.module.css";

interface ArticlesTableProps {
  rows: ArticleSummary[] | undefined;
  loading: boolean;
  error: unknown;
  onRetry: () => void;
  sort?: string;
  onSort: (field: string) => void;
  empty: ReactNode;
  footer: ReactNode;
}

const COLUMNS: Column<ArticleSummary>[] = [
  { key: "code", header: "Code", sortKey: "code", width: "120px", render: (a) => <strong>{a.code}</strong> },
  {
    key: "designation",
    header: "Désignation",
    sortKey: "designation",
    render: (a) => (
      <div className={styles.designation}>
        <span>{a.designation}</span>
        {a.codeBarres && <span className={styles.barcode}>{a.codeBarres}</span>}
      </div>
    ),
  },
  { key: "famille", header: "Famille", render: (a) => a.familleLibelle ?? "—" },
  { key: "type", header: "Type", render: (a) => <ArticleTypeBadge type={a.type} /> },
  { key: "achat", header: "Prix achat HT", sortKey: "prixAchatHt", align: "right", render: (a) => formatMoney(a.prixAchatHt) },
  { key: "vente", header: "Prix vente HT", sortKey: "prixVenteHt", align: "right", render: (a) => formatMoney(a.prixVenteHt) },
  { key: "pump", header: "PUMP", align: "right", render: (a) => (a.type === "STOCKE" ? formatMoney(a.pump) : "—") },
  { key: "tva", header: "TVA", align: "right", render: (a) => formatPercent(a.tauxTva) },
  { key: "actif", header: "Statut", render: (a) => (a.actif ? <Badge tone="success">Actif</Badge> : <Badge>Inactif</Badge>) },
];

export function ArticlesTable({ rows, loading, error, onRetry, sort, onSort, empty, footer }: ArticlesTableProps) {
  const router = useRouter();
  return (
    <Table
      columns={COLUMNS}
      rows={rows}
      rowKey={(a) => a.id}
      loading={loading}
      error={error}
      onRetry={onRetry}
      sort={sort}
      onSort={onSort}
      onRowClick={(a) => router.push(`/articles/${a.id}`)}
      empty={empty}
      footer={footer}
    />
  );
}
