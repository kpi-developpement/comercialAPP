"use client";

import { FilterBar, FilterItem } from "@/components/layout/FilterBar";
import { PageHeader } from "@/components/layout/PageHeader";
import { Card } from "@/components/ui/Card";
import { Checkbox } from "@/components/ui/Checkbox";
import { Input } from "@/components/ui/Input";
import { Pagination } from "@/components/ui/Pagination";
import { Select } from "@/components/ui/Select";
import { useFamilleOptions } from "@/features/familles/hooks/useFamilles";
import { useDebouncedValue } from "@/lib/use-debounced-value";
import { useListParams } from "@/lib/use-list-params";
import { useDepotOptions } from "../../hooks/useDepots";
import { useEtatStock } from "../../hooks/useStockReports";
import { EtatStockTable } from "../EtatStockTable";

export function EtatStockView() {
  const list = useListParams({ search: "", depotId: "", familleId: "", sousMinimum: false as boolean }, "code,asc", 50);
  const search = useDebouncedValue(list.filters.search);
  const query = useEtatStock({ ...list.params, search, sousMinimum: list.filters.sousMinimum || undefined });
  const { options: depots } = useDepotOptions(false);
  const { data: familles = [] } = useFamilleOptions();

  return (
    <>
      <PageHeader title="État du stock" subtitle="Quantités par article, valorisées au PUMP" />
      <Card padded={false}>
        <FilterBar>
          <FilterItem grow>
            <Input type="search" aria-label="Rechercher" placeholder="Code ou désignation" value={list.filters.search}
              onChange={(e) => list.updateFilter("search", e.target.value)} />
          </FilterItem>
          <FilterItem>
            <Select aria-label="Dépôt" options={depots} placeholder="Tous les dépôts" value={list.filters.depotId}
              onChange={(e) => list.updateFilter("depotId", e.target.value)} />
          </FilterItem>
          <FilterItem>
            <Select aria-label="Famille" options={familles} placeholder="Toutes les familles" value={list.filters.familleId}
              onChange={(e) => list.updateFilter("familleId", e.target.value)} />
          </FilterItem>
          <FilterItem>
            <Checkbox label="Sous le minimum" checked={list.filters.sousMinimum}
              onChange={(e) => list.updateFilter("sousMinimum", e.target.checked)} />
          </FilterItem>
        </FilterBar>
        <EtatStockTable
          rows={query.data?.content}
          loading={query.isLoading}
          error={query.error}
          onRetry={() => query.refetch()}
          sort={list.sort}
          onSort={list.toggleSort}
          footer={query.data && (
            <Pagination page={query.data.page} totalPages={query.data.totalPages} totalElements={query.data.totalElements}
              size={list.size} onPageChange={list.setPage} onSizeChange={list.setSize} />
          )}
        />
      </Card>
    </>
  );
}
