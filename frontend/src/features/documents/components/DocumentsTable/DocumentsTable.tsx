"use client";

import { useRouter } from "next/navigation";
import type { ReactNode } from "react";
import { Table, type Column } from "@/components/ui/Table";
import { formatDate, formatMoney } from "@/lib/format";
import { routeOf, type DocumentTypeConfig } from "../../config/documentTypes";
import type { DocumentSummary } from "../../types/document.types";
import { DocumentStatusBadge, PaymentStatusBadge } from "../DocumentStatusBadge";
import styles from "./DocumentsTable.module.css";

interface DocumentsTableProps {
  config: DocumentTypeConfig;
  rows: DocumentSummary[] | undefined;
  loading: boolean;
  error: unknown;
  onRetry: () => void;
  sort?: string;
  onSort: (field: string) => void;
  empty: ReactNode;
  footer: ReactNode;
}

function columns(config: DocumentTypeConfig): Column<DocumentSummary>[] {
  const cols: Column<DocumentSummary>[] = [
    {
      key: "numero",
      header: "Numéro",
      sortKey: "numero",
      width: "150px",
      render: (d) => (d.numero ? <strong>{d.numero}</strong> : <span className={styles.draft}>Brouillon</span>),
    },
    { key: "date", header: "Date", sortKey: "dateDocument", width: "110px", render: (d) => formatDate(d.dateDocument) },
    {
      key: "tiers",
      header: config.tiersKind === "clients" ? "Client" : "Fournisseur",
      sortKey: "tiersNom",
      render: (d) => (
        <div className={styles.tiers}>
          <span>{d.tiersNom}</span>
          {d.sources && <span className={styles.sources}>Origine : {d.sources}</span>}
        </div>
      ),
    },
    { key: "statut", header: "Statut", render: (d) => <DocumentStatusBadge statut={d.statut} /> },
    { key: "ht", header: "Total HT", sortKey: "totalHt", align: "right", render: (d) => formatMoney(d.totalHt) },
    { key: "ttc", header: "Total TTC", sortKey: "totalTtc", align: "right", render: (d) => formatMoney(d.totalTtc) },
  ];
  if (config.isFacture) {
    cols.push(
      { key: "echeance", header: "Échéance", sortKey: "dateEcheance", render: (d) => formatDate(d.dateEcheance) },
      { key: "reste", header: "Reste à payer", align: "right", render: (d) => (d.numero ? formatMoney(d.resteAPayer) : "—") },
      { key: "paiement", header: "Paiement", render: (d) => <PaymentStatusBadge statut={d.statutPaiement} /> },
    );
  }
  return cols;
}

export function DocumentsTable({ config, rows, loading, error, onRetry, sort, onSort, empty, footer }: DocumentsTableProps) {
  const router = useRouter();
  return (
    <Table
      columns={columns(config)}
      rows={rows}
      rowKey={(d) => d.id}
      loading={loading}
      error={error}
      onRetry={onRetry}
      sort={sort}
      onSort={onSort}
      onRowClick={(d) => router.push(routeOf(config.type, d.id))}
      empty={empty}
      footer={footer}
    />
  );
}
