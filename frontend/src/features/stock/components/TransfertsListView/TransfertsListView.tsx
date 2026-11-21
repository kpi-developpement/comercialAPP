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
import { useAuth } from "@/features/auth/components/AuthProvider";
import { formatDate } from "@/lib/format";
import { useDebouncedValue } from "@/lib/use-debounced-value";
import { useListParams } from "@/lib/use-list-params";
import { useDepotOptions } from "../../hooks/useDepots";
import { useTransferts } from "../../hooks/useTransferts";
import type { Transfert } from "../../types/stock.types";
import { OperationStatusBadge } from "../OperationStatusBadge";

const COLUMNS: Column<Transfert>[] = [
  { key: "numero", header: "Numéro", sortKey: "numero", render: (t) => (t.numero ? <strong>{t.numero}</strong> : <em>Brouillon</em>) },
  { key: "date", header: "Date", sortKey: "dateTransfert", render: (t) => formatDate(t.dateTransfert) },
  { key: "source", header: "De", render: (t) => t.depotSourceLibelle },
  { key: "dest", header: "Vers", render: (t) => t.depotDestinationLibelle },
  { key: "lignes", header: "Lignes", align: "right", render: (t) => t.lignes.length },
  { key: "statut", header: "Statut", render: (t) => <OperationStatusBadge statut={t.statut} /> },
];

export function TransfertsListView() {
  const router = useRouter();
  const { can } = useAuth();
  const list = useListParams({ search: "", depotId: "", statut: "" }, "dateTransfert,desc");
  const search = useDebouncedValue(list.filters.search);
  const query = useTransferts({ ...list.params, search });
  const { options: depots } = useDepotOptions(false);

  return (
    <>
      <PageHeader
        title="Transferts entre dépôts"
        actions={can("stock.write") && (
          <Link href="/stock/transferts/nouveau">
            <Button variant="primary" icon={<Icon name="plus" size={16} />}>Nouveau transfert</Button>
          </Link>
        )}
      />
      <Card padded={false}>
        <FilterBar>
          <FilterItem grow>
            <Input type="search" aria-label="Rechercher" placeholder="Numéro ou notes" value={list.filters.search}
              onChange={(e) => list.updateFilter("search", e.target.value)} />
          </FilterItem>
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
          rowKey={(t) => t.id}
          loading={query.isLoading}
          error={query.error}
          onRetry={() => query.refetch()}
          sort={list.sort}
          onSort={list.toggleSort}
          onRowClick={(t) => router.push(`/stock/transferts/${t.id}`)}
          empty={<EmptyState title="Aucun transfert" />}
          footer={query.data && (
            <Pagination page={query.data.page} totalPages={query.data.totalPages} totalElements={query.data.totalElements}
              size={list.size} onPageChange={list.setPage} onSizeChange={list.setSize} />
          )}
        />
      </Card>
    </>
  );
}
