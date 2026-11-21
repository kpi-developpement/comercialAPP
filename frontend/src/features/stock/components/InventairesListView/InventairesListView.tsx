"use client";

import { useRouter } from "next/navigation";
import { useState } from "react";
import { FilterBar, FilterItem } from "@/components/layout/FilterBar";
import { PageHeader } from "@/components/layout/PageHeader";
import { Button } from "@/components/ui/Button";
import { Card } from "@/components/ui/Card";
import { EmptyState } from "@/components/ui/EmptyState";
import { Icon } from "@/components/ui/Icon";
import { Pagination } from "@/components/ui/Pagination";
import { Select } from "@/components/ui/Select";
import { Table, type Column } from "@/components/ui/Table";
import { useAuth } from "@/features/auth/components/AuthProvider";
import { formatDate } from "@/lib/format";
import { useListParams } from "@/lib/use-list-params";
import { useDepotOptions } from "../../hooks/useDepots";
import { useInventaires } from "../../hooks/useInventaires";
import type { InventaireSummary } from "../../types/stock.types";
import { InventaireNewModal } from "../InventaireNewModal";
import { OperationStatusBadge } from "../OperationStatusBadge";

const COLUMNS: Column<InventaireSummary>[] = [
  { key: "numero", header: "Numéro", sortKey: "numero", render: (i) => (i.numero ? <strong>{i.numero}</strong> : <em>Brouillon</em>) },
  { key: "date", header: "Date", sortKey: "dateInventaire", render: (i) => formatDate(i.dateInventaire) },
  { key: "depot", header: "Dépôt", render: (i) => i.depotLibelle },
  { key: "lignes", header: "Articles", align: "right", render: (i) => i.nbLignes },
  { key: "statut", header: "Statut", render: (i) => <OperationStatusBadge statut={i.statut} /> },
  { key: "user", header: "Créé par", render: (i) => i.createdBy ?? "—" },
];

export function InventairesListView() {
  const router = useRouter();
  const { can } = useAuth();
  const list = useListParams({ depotId: "", statut: "" }, "dateInventaire,desc");
  const query = useInventaires(list.params);
  const { options: depots } = useDepotOptions(false);
  const [creating, setCreating] = useState(false);

  return (
    <>
      <PageHeader
        title="Inventaires"
        subtitle="Comptage physique par dépôt et ajustement des écarts"
        actions={can("stock.write") && (
          <Button variant="primary" icon={<Icon name="plus" size={16} />} onClick={() => setCreating(true)}>Nouvel inventaire</Button>
        )}
      />
      <Card padded={false}>
        <FilterBar>
          <FilterItem>
            <Select aria-label="Dépôt" options={depots} placeholder="Tous les dépôts" value={list.filters.depotId}
              onChange={(e) => list.updateFilter("depotId", e.target.value)} />
          </FilterItem>
          <FilterItem>
            <Select aria-label="Statut" placeholder="Tous les statuts"
              options={[{ value: "BROUILLON", label: "Brouillon" }, { value: "VALIDE", label: "Validé" }]}
              value={list.filters.statut} onChange={(e) => list.updateFilter("statut", e.target.value)} />
          </FilterItem>
        </FilterBar>
        <Table
          columns={COLUMNS}
          rows={query.data?.content}
          rowKey={(i) => i.id}
          loading={query.isLoading}
          error={query.error}
          onRetry={() => query.refetch()}
          sort={list.sort}
          onSort={list.toggleSort}
          onRowClick={(i) => router.push(`/stock/inventaires/${i.id}`)}
          empty={<EmptyState title="Aucun inventaire" />}
          footer={query.data && (
            <Pagination page={query.data.page} totalPages={query.data.totalPages} totalElements={query.data.totalElements}
              size={list.size} onPageChange={list.setPage} onSizeChange={list.setSize} />
          )}
        />
      </Card>
      <InventaireNewModal open={creating} onClose={() => setCreating(false)} />
    </>
  );
}
