"use client";

import { useSearchParams } from "next/navigation";
import { FilterBar, FilterItem } from "@/components/layout/FilterBar";
import { PageHeader } from "@/components/layout/PageHeader";
import { Badge } from "@/components/ui/Badge";
import { Card } from "@/components/ui/Card";
import { Input } from "@/components/ui/Input";
import { Pagination } from "@/components/ui/Pagination";
import { Select } from "@/components/ui/Select";
import { Table, type Column } from "@/components/ui/Table";
import { formatDateTime, formatMoney, formatQty } from "@/lib/format";
import { useDebouncedValue } from "@/lib/use-debounced-value";
import { useListParams } from "@/lib/use-list-params";
import { useDepotOptions } from "../../hooks/useDepots";
import { useMouvements } from "../../hooks/useStockReports";
import { TYPE_MOUVEMENT_LABELS, type Mouvement, type TypeMouvement } from "../../types/stock.types";

const TYPES = (Object.keys(TYPE_MOUVEMENT_LABELS) as TypeMouvement[]).map((t) => ({ value: t, label: TYPE_MOUVEMENT_LABELS[t] }));

const COLUMNS: Column<Mouvement>[] = [
  { key: "date", header: "Date", sortKey: "dateMouvement", width: "150px", render: (m) => formatDateTime(m.dateMouvement) },
  { key: "article", header: "Article", render: (m) => <><strong>{m.articleCode}</strong> {m.articleDesignation}</> },
  { key: "depot", header: "Dépôt", render: (m) => m.depotLibelle },
  { key: "type", header: "Type", render: (m) => <Badge tone={Number(m.quantite) >= 0 ? "success" : "warning"}>{TYPE_MOUVEMENT_LABELS[m.type]}</Badge> },
  { key: "qte", header: "Quantité", align: "right", render: (m) => (Number(m.quantite) > 0 ? "+" : "") + formatQty(m.quantite) },
  { key: "cout", header: "Coût unit.", align: "right", render: (m) => formatMoney(m.coutUnitaire) },
  { key: "stock", header: "Stock après", align: "right", render: (m) => formatQty(m.stockApres) },
  { key: "pump", header: "PUMP après", align: "right", render: (m) => formatMoney(m.pumpApres) },
  { key: "ref", header: "Référence", render: (m) => m.referenceNumero ?? "—" },
  { key: "user", header: "Par", render: (m) => m.createdBy ?? "—" },
];

export function MouvementsView() {
  const searchParams = useSearchParams();
  const list = useListParams(
    { search: "", depotId: "", type: "", dateDebut: "", dateFin: "", articleId: searchParams.get("articleId") ?? "" },
    "dateMouvement,desc",
    50,
  );
  const search = useDebouncedValue(list.filters.search);
  const query = useMouvements({ ...list.params, search });
  const { options: depots } = useDepotOptions(false);

  return (
    <>
      <PageHeader title="Mouvements de stock" subtitle="Journal de toutes les entrées et sorties" />
      <Card padded={false}>
        <FilterBar>
          <FilterItem grow>
            <Input type="search" aria-label="Référence" placeholder="N° de document" value={list.filters.search}
              onChange={(e) => list.updateFilter("search", e.target.value)} />
          </FilterItem>
          <FilterItem>
            <Select aria-label="Dépôt" options={depots} placeholder="Tous les dépôts" value={list.filters.depotId}
              onChange={(e) => list.updateFilter("depotId", e.target.value)} />
          </FilterItem>
          <FilterItem>
            <Select aria-label="Type" options={TYPES} placeholder="Tous les types" value={list.filters.type}
              onChange={(e) => list.updateFilter("type", e.target.value)} />
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
          rowKey={(m) => m.id}
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
