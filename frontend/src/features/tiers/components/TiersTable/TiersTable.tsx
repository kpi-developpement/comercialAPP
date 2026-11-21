"use client";

import { useRouter } from "next/navigation";
import type { ReactNode } from "react";
import { Badge } from "@/components/ui/Badge";
import { Table, type Column } from "@/components/ui/Table";
import type { TiersKind, TiersSummary } from "../../types/tiers.types";

interface TiersTableProps {
  kind: TiersKind;
  rows: TiersSummary[] | undefined;
  loading: boolean;
  error: unknown;
  onRetry: () => void;
  sort?: string;
  onSort: (field: string) => void;
  empty: ReactNode;
  footer: ReactNode;
}

const COLUMNS: Column<TiersSummary>[] = [
  { key: "code", header: "Code", sortKey: "code", width: "120px", render: (t) => <strong>{t.code}</strong> },
  { key: "rs", header: "Raison sociale", sortKey: "raisonSociale", render: (t) => t.raisonSociale },
  { key: "ice", header: "ICE", render: (t) => t.ice ?? "—" },
  { key: "ville", header: "Ville", sortKey: "ville", render: (t) => t.ville ?? "—" },
  { key: "tel", header: "Téléphone", render: (t) => t.telephone ?? "—" },
  { key: "email", header: "E-mail", render: (t) => t.email ?? "—" },
  {
    key: "actif",
    header: "Statut",
    render: (t) => (
      <>
        {t.actif ? <Badge tone="success">Actif</Badge> : <Badge>Inactif</Badge>}{" "}
        {t.prospect && <Badge tone="info">Prospect</Badge>} {t.bloque && <Badge tone="danger">Bloqué</Badge>}
      </>
    ),
  },
];

export function TiersTable({ kind, rows, loading, error, onRetry, sort, onSort, empty, footer }: TiersTableProps) {
  const router = useRouter();
  return (
    <Table
      columns={COLUMNS}
      rows={rows}
      rowKey={(t) => t.id}
      loading={loading}
      error={error}
      onRetry={onRetry}
      sort={sort}
      onSort={onSort}
      onRowClick={(t) => router.push(`/${kind}/${t.id}`)}
      empty={empty}
      footer={footer}
    />
  );
}
