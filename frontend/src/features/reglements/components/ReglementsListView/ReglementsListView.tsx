"use client";

import Link from "next/link";
import { useRouter } from "next/navigation";
import { FilterBar, FilterItem } from "@/components/layout/FilterBar";
import { PageHeader } from "@/components/layout/PageHeader";
import { Button } from "@/components/ui/Button";
import { Card } from "@/components/ui/Card";
import { EmptyState } from "@/components/ui/EmptyState";
import { Icon } from "@/components/ui/Icon";
import { Input } from "@/components/ui/Input";
import { Pagination } from "@/components/ui/Pagination";
import { Select } from "@/components/ui/Select";
import { Table, type Column } from "@/components/ui/Table";
import { RequireAuth } from "@/features/auth/components/RequireAuth";
import { useAuth } from "@/features/auth/components/AuthProvider";
import { formatDate, formatMoney } from "@/lib/format";
import { TYPE_REGLEMENT_LABELS, type TypeReglement } from "@/lib/types";
import { useDebouncedValue } from "@/lib/use-debounced-value";
import { useListParams } from "@/lib/use-list-params";
import { useReglements } from "../../hooks/useReglements";
import { STATUT_REGLEMENT_LABELS, type Reglement, type Sens, type StatutReglement } from "../../types/reglement.types";
import { ReglementStatusBadge } from "../ReglementStatusBadge";

const MODES = (Object.keys(TYPE_REGLEMENT_LABELS) as TypeReglement[]).map((m) => ({ value: m, label: TYPE_REGLEMENT_LABELS[m] }));
const STATUTS = (Object.keys(STATUT_REGLEMENT_LABELS) as StatutReglement[]).map((s) => ({ value: s, label: STATUT_REGLEMENT_LABELS[s] }));

const COLUMNS: Column<Reglement>[] = [
  { key: "numero", header: "Numéro", sortKey: "numero", width: "150px", render: (r) => <strong>{r.numero}</strong> },
  { key: "date", header: "Date", sortKey: "dateReglement", render: (r) => formatDate(r.dateReglement) },
  { key: "tiers", header: "Tiers", sortKey: "tiersNom", render: (r) => r.tiersNom },
  { key: "mode", header: "Mode", render: (r) => TYPE_REGLEMENT_LABELS[r.mode] },
  { key: "ref", header: "Référence", render: (r) => r.reference ?? "—" },
  { key: "echeance", header: "Échéance", sortKey: "dateEcheance", render: (r) => formatDate(r.dateEcheance) },
  { key: "statut", header: "Statut", render: (r) => <ReglementStatusBadge statut={r.statut} /> },
  { key: "montant", header: "Montant", sortKey: "montant", align: "right", render: (r) => formatMoney(r.montant) },
  { key: "nonAffecte", header: "Non affecté", align: "right", render: (r) => (Number(r.montantNonAffecte) > 0 ? formatMoney(r.montantNonAffecte) : "—") },
];

export function ReglementsListView({ sens }: { sens: Sens }) {
  const router = useRouter();
  const { can } = useAuth();
  const list = useListParams({ search: "", mode: "", statut: "", dateDebut: "", dateFin: "" }, "dateReglement,desc");
  const search = useDebouncedValue(list.filters.search);
  const query = useReglements(sens, { ...list.params, search });
  const title = sens === "clients" ? "Règlements clients" : "Règlements fournisseurs";

  return (
    <RequireAuth permission="reglement.read">
      <PageHeader
        title={title}
        actions={can("reglement.write") && (
          <Link href={`/reglements/${sens}/nouveau`}>
            <Button variant="primary" icon={<Icon name="plus" size={16} />}>Nouveau règlement</Button>
          </Link>
        )}
      />
      <Card padded={false}>
        <FilterBar>
          <FilterItem grow>
            <Input type="search" aria-label="Rechercher" placeholder="Numéro, tiers, n° de chèque, banque"
              value={list.filters.search} onChange={(e) => list.updateFilter("search", e.target.value)} />
          </FilterItem>
          <FilterItem>
            <Select aria-label="Mode" options={MODES} placeholder="Tous les modes" value={list.filters.mode}
              onChange={(e) => list.updateFilter("mode", e.target.value)} />
          </FilterItem>
          <FilterItem>
            <Select aria-label="Statut" options={STATUTS} placeholder="Tous les statuts" value={list.filters.statut}
              onChange={(e) => list.updateFilter("statut", e.target.value)} />
          </FilterItem>
          <FilterItem>
            <Input type="date" aria-label="Du" value={list.filters.dateDebut} onChange={(e) => list.updateFilter("dateDebut", e.target.value)} />
          </FilterItem>
          <FilterItem>
            <Input type="date" aria-label="Au" value={list.filters.dateFin} onChange={(e) => list.updateFilter("dateFin", e.target.value)} />
          </FilterItem>
        </FilterBar>
        <Table
          columns={COLUMNS}
          rows={query.data?.content}
          rowKey={(r) => r.id}
          loading={query.isLoading}
          error={query.error}
          onRetry={() => query.refetch()}
          sort={list.sort}
          onSort={list.toggleSort}
          onRowClick={(r) => router.push(`/reglements/${sens}/${r.id}`)}
          empty={<EmptyState title="Aucun règlement" />}
          footer={query.data && (
            <Pagination page={query.data.page} totalPages={query.data.totalPages} totalElements={query.data.totalElements}
              size={list.size} onPageChange={list.setPage} onSizeChange={list.setSize} />
          )}
        />
      </Card>
    </RequireAuth>
  );
}
