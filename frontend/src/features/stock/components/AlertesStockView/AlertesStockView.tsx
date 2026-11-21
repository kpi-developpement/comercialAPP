"use client";

import { FilterBar, FilterItem } from "@/components/layout/FilterBar";
import { PageHeader } from "@/components/layout/PageHeader";
import { Card } from "@/components/ui/Card";
import { EmptyState } from "@/components/ui/EmptyState";
import { Input } from "@/components/ui/Input";
import { Pagination } from "@/components/ui/Pagination";
import { useDebouncedValue } from "@/lib/use-debounced-value";
import { useListParams } from "@/lib/use-list-params";
import { useAlertesStock } from "../../hooks/useStockReports";
import { EtatStockTable } from "../EtatStockTable";

export function AlertesStockView() {
  const list = useListParams({ search: "" }, "code,asc");
  const search = useDebouncedValue(list.filters.search);
  const query = useAlertesStock({ ...list.params, search });
  return (
    <>
      <PageHeader title="Alertes de stock" subtitle="Articles dont le stock total est inférieur au stock minimum" />
      <Card padded={false}>
        <FilterBar>
          <FilterItem grow>
            <Input type="search" aria-label="Rechercher" placeholder="Code ou désignation" value={list.filters.search}
              onChange={(e) => list.updateFilter("search", e.target.value)} />
          </FilterItem>
        </FilterBar>
        <EtatStockTable
          rows={query.data?.content}
          loading={query.isLoading}
          error={query.error}
          onRetry={() => query.refetch()}
          sort={list.sort}
          onSort={list.toggleSort}
          empty={<EmptyState title="Aucune alerte" description="Tous les articles sont au-dessus de leur stock minimum." />}
          footer={query.data && (
            <Pagination page={query.data.page} totalPages={query.data.totalPages} totalElements={query.data.totalElements}
              size={list.size} onPageChange={list.setPage} onSizeChange={list.setSize} />
          )}
        />
      </Card>
    </>
  );
}
