"use client";

import { useState } from "react";
import { FilterBar, FilterItem } from "@/components/layout/FilterBar";
import { PageHeader } from "@/components/layout/PageHeader";
import { Badge } from "@/components/ui/Badge";
import { Button } from "@/components/ui/Button";
import { Card } from "@/components/ui/Card";
import { ConfirmDialog } from "@/components/ui/ConfirmDialog";
import { Icon } from "@/components/ui/Icon";
import { Input } from "@/components/ui/Input";
import { Pagination } from "@/components/ui/Pagination";
import { Table, type Column } from "@/components/ui/Table";
import { useToast } from "@/components/ui/Toast";
import { useAuth } from "@/features/auth/components/AuthProvider";
import { errorMessage } from "@/lib/api-client";
import { useDebouncedValue } from "@/lib/use-debounced-value";
import { useListParams } from "@/lib/use-list-params";
import { useDeleteDepot, useDepots } from "../../hooks/useDepots";
import type { Depot } from "../../types/stock.types";
import { DepotFormModal } from "../DepotFormModal";

export function DepotsView() {
  const { can } = useAuth();
  const toast = useToast();
  const canWrite = can("stock.write");
  const list = useListParams({ search: "" }, "code,asc");
  const search = useDebouncedValue(list.filters.search);
  const query = useDepots({ ...list.params, search });
  const remove = useDeleteDepot();
  const [editing, setEditing] = useState<Depot | null>(null);
  const [formOpen, setFormOpen] = useState(false);
  const [toDelete, setToDelete] = useState<Depot | null>(null);

  const columns: Column<Depot>[] = [
    { key: "code", header: "Code", sortKey: "code", width: "140px", render: (d) => <strong>{d.code}</strong> },
    { key: "libelle", header: "Libellé", sortKey: "libelle", render: (d) => d.libelle },
    { key: "adresse", header: "Adresse", render: (d) => d.adresse ?? "—" },
    {
      key: "statut",
      header: "Statut",
      render: (d) => (
        <>
          {d.actif ? <Badge tone="success">Actif</Badge> : <Badge>Inactif</Badge>} {d.parDefaut && <Badge tone="primary">Par défaut</Badge>}
        </>
      ),
    },
    {
      key: "actions",
      header: "",
      align: "right",
      render: (d) =>
        canWrite && (
          <Button size="sm" variant="ghost" aria-label={`Supprimer ${d.code}`} onClick={(e) => { e.stopPropagation(); setToDelete(d); }}>
            <Icon name="trash" size={15} />
          </Button>
        ),
    },
  ];

  const onDelete = async () => {
    if (!toDelete) return;
    try {
      await remove.mutateAsync(toDelete.id);
      toast.success(`Dépôt ${toDelete.code} supprimé`);
    } catch (e) {
      toast.error(errorMessage(e));
    } finally {
      setToDelete(null);
    }
  };

  return (
    <>
      <PageHeader
        title="Dépôts"
        actions={canWrite && (
          <Button variant="primary" icon={<Icon name="plus" size={16} />} onClick={() => { setEditing(null); setFormOpen(true); }}>
            Nouveau dépôt
          </Button>
        )}
      />
      <Card padded={false}>
        <FilterBar>
          <FilterItem grow>
            <Input type="search" aria-label="Rechercher" placeholder="Code ou libellé" value={list.filters.search}
              onChange={(e) => list.updateFilter("search", e.target.value)} />
          </FilterItem>
        </FilterBar>
        <Table
          columns={columns}
          rows={query.data?.content}
          rowKey={(d) => d.id}
          loading={query.isLoading}
          error={query.error}
          onRetry={() => query.refetch()}
          sort={list.sort}
          onSort={list.toggleSort}
          onRowClick={canWrite ? (d) => { setEditing(d); setFormOpen(true); } : undefined}
          footer={query.data && (
            <Pagination page={query.data.page} totalPages={query.data.totalPages} totalElements={query.data.totalElements}
              size={list.size} onPageChange={list.setPage} onSizeChange={list.setSize} />
          )}
        />
      </Card>
      <DepotFormModal open={formOpen} depot={editing} onClose={() => setFormOpen(false)} />
      <ConfirmDialog
        open={!!toDelete}
        title="Supprimer le dépôt"
        message={`Supprimer le dépôt ${toDelete?.libelle ?? ""} ? Impossible s'il a déjà du stock ou des documents.`}
        confirmLabel="Supprimer le dépôt"
        danger
        loading={remove.isPending}
        onConfirm={onDelete}
        onCancel={() => setToDelete(null)}
      />
    </>
  );
}
