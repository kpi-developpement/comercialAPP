"use client";

import { useRouter } from "next/navigation";
import { useState } from "react";
import { FilterBar, FilterItem } from "@/components/layout/FilterBar";
import { PageHeader } from "@/components/layout/PageHeader";
import { Badge } from "@/components/ui/Badge";
import { Card } from "@/components/ui/Card";
import { Checkbox } from "@/components/ui/Checkbox";
import { EmptyState } from "@/components/ui/EmptyState";
import { Input } from "@/components/ui/Input";
import { Pagination } from "@/components/ui/Pagination";
import { Table, type Column } from "@/components/ui/Table";
import { Tabs } from "@/components/ui/Tabs";
import { RequireAuth } from "@/features/auth/components/RequireAuth";
import { PaymentStatusBadge } from "@/features/documents/components/DocumentStatusBadge";
import { routeOf } from "@/features/documents/config/documentTypes";
import type { DocumentSummary } from "@/features/documents/types/document.types";
import { formatDate, formatMoney } from "@/lib/format";
import { useDebouncedValue } from "@/lib/use-debounced-value";
import { useListParams } from "@/lib/use-list-params";
import { useEcheancier } from "../../hooks/useReglements";
import type { Sens } from "../../types/reglement.types";

function joursRetard(echeance: string | null): number {
  if (!echeance) return 0;
  const diff = Date.now() - new Date(`${echeance}T00:00:00`).getTime();
  return Math.max(0, Math.floor(diff / 86_400_000));
}

const COLUMNS: Column<DocumentSummary>[] = [
  { key: "numero", header: "Facture", sortKey: "numero", render: (d) => <strong>{d.numero}</strong> },
  { key: "tiers", header: "Tiers", sortKey: "tiersNom", render: (d) => d.tiersNom },
  { key: "date", header: "Date", sortKey: "dateDocument", render: (d) => formatDate(d.dateDocument) },
  { key: "echeance", header: "Échéance", sortKey: "dateEcheance", render: (d) => formatDate(d.dateEcheance) },
  {
    key: "retard",
    header: "Retard",
    render: (d) => {
      const j = joursRetard(d.dateEcheance);
      return j > 0 ? <Badge tone={j > 60 ? "danger" : "warning"}>{j} j</Badge> : <Badge>À échoir</Badge>;
    },
  },
  { key: "ttc", header: "Total TTC", align: "right", render: (d) => formatMoney(d.totalTtc) },
  { key: "reste", header: "Reste à payer", align: "right", render: (d) => <strong>{formatMoney(d.resteAPayer)}</strong> },
  { key: "statut", header: "Paiement", render: (d) => <PaymentStatusBadge statut={d.statutPaiement} /> },
];

export function EcheancierView() {
  const router = useRouter();
  const [sens, setSens] = useState<Sens>("clients");
  const list = useListParams({ search: "", enRetard: false as boolean }, "dateEcheance,asc");
  const search = useDebouncedValue(list.filters.search);
  const query = useEcheancier(sens, { ...list.params, search, enRetard: list.filters.enRetard || undefined });

  return (
    <RequireAuth permission="reglement.read">
      <PageHeader title="Échéancier" subtitle="Factures non soldées, par date d'échéance" />
      <Card padded={false}>
        <Tabs items={[{ key: "clients", label: "Clients (à encaisser)" }, { key: "fournisseurs", label: "Fournisseurs (à payer)" }]}
          active={sens} onChange={(k) => { setSens(k); list.setPage(0); }} />
        <FilterBar>
          <FilterItem grow>
            <Input type="search" aria-label="Rechercher" placeholder="Numéro ou tiers" value={list.filters.search}
              onChange={(e) => list.updateFilter("search", e.target.value)} />
          </FilterItem>
          <FilterItem>
            <Checkbox label="En retard uniquement" checked={list.filters.enRetard}
              onChange={(e) => list.updateFilter("enRetard", e.target.checked)} />
          </FilterItem>
        </FilterBar>
        <Table
          columns={COLUMNS}
          rows={query.data?.content}
          rowKey={(d) => d.id}
          loading={query.isLoading}
          error={query.error}
          onRetry={() => query.refetch()}
          sort={list.sort}
          onSort={list.toggleSort}
          onRowClick={(d) => router.push(routeOf(d.type, d.id))}
          empty={<EmptyState title="Aucune facture en attente" />}
          footer={query.data && (
            <Pagination page={query.data.page} totalPages={query.data.totalPages} totalElements={query.data.totalElements}
              size={list.size} onPageChange={list.setPage} onSizeChange={list.setSize} />
          )}
        />
      </Card>
    </RequireAuth>
  );
}
