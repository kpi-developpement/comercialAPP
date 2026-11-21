"use client";

import Link from "next/link";
import { FilterBar, FilterItem } from "@/components/layout/FilterBar";
import { PageHeader } from "@/components/layout/PageHeader";
import { Button } from "@/components/ui/Button";
import { Card } from "@/components/ui/Card";
import { EmptyState } from "@/components/ui/EmptyState";
import { Icon } from "@/components/ui/Icon";
import { Input } from "@/components/ui/Input";
import { Pagination } from "@/components/ui/Pagination";
import { Select } from "@/components/ui/Select";
import { useAuth } from "@/features/auth/components/AuthProvider";
import { useDebouncedValue } from "@/lib/use-debounced-value";
import { useListParams } from "@/lib/use-list-params";
import { useTiersList } from "../../hooks/useTiers";
import { TIERS_LABELS, type TiersFilters, type TiersKind } from "../../types/tiers.types";
import { TiersTable } from "../TiersTable";

const INITIAL: TiersFilters = { search: "", ville: "", actif: "" };

export function TiersListView({ kind }: { kind: TiersKind }) {
  const { can } = useAuth();
  const labels = TIERS_LABELS[kind];
  const list = useListParams<TiersFilters>(INITIAL, "raisonSociale,asc");
  const search = useDebouncedValue(list.filters.search);
  const ville = useDebouncedValue(list.filters.ville);
  const query = useTiersList(kind, { ...list.params, search, ville });
  const canWrite = can(kind === "clients" ? "client.write" : "fournisseur.write");
  const hasFilters = Object.values(list.filters).some((v) => v !== "");

  return (
    <>
      <PageHeader
        title={labels.plural}
        subtitle={query.data ? `${query.data.totalElements} ${labels.plural.toLowerCase()}` : undefined}
        actions={canWrite && (
          <Link href={`/${kind}/nouveau`}>
            <Button variant="primary" icon={<Icon name="plus" size={16} />}>{labels.nouveau}</Button>
          </Link>
        )}
      />
      <Card padded={false}>
        <FilterBar>
          <FilterItem grow>
            <Input type="search" aria-label="Rechercher" placeholder="Code, raison sociale, ICE ou téléphone"
              value={list.filters.search} onChange={(e) => list.updateFilter("search", e.target.value)} />
          </FilterItem>
          <FilterItem>
            <Input aria-label="Ville" placeholder="Ville" value={list.filters.ville}
              onChange={(e) => list.updateFilter("ville", e.target.value)} />
          </FilterItem>
          <FilterItem>
            <Select aria-label="Statut" placeholder="Actifs et inactifs"
              options={[{ value: "true", label: "Actifs" }, { value: "false", label: "Inactifs" }]}
              value={list.filters.actif} onChange={(e) => list.updateFilter("actif", e.target.value)} />
          </FilterItem>
        </FilterBar>
        <TiersTable
          kind={kind}
          rows={query.data?.content}
          loading={query.isLoading}
          error={query.error}
          onRetry={() => query.refetch()}
          sort={list.sort}
          onSort={list.toggleSort}
          empty={hasFilters
            ? <EmptyState title="Aucun résultat" action={<Button onClick={() => list.setFilters(INITIAL)}>Effacer les filtres</Button>} />
            : <EmptyState title={`Aucun ${labels.singular.toLowerCase()}`} />}
          footer={query.data && (
            <Pagination page={query.data.page} totalPages={query.data.totalPages} totalElements={query.data.totalElements}
              size={list.size} onPageChange={list.setPage} onSizeChange={list.setSize} />
          )}
        />
      </Card>
    </>
  );
}
