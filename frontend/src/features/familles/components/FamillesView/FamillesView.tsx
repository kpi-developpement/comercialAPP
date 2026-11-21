"use client";

import { useState } from "react";
import { FilterBar, FilterItem } from "@/components/layout/FilterBar";
import { PageHeader } from "@/components/layout/PageHeader";
import { Button } from "@/components/ui/Button";
import { Card } from "@/components/ui/Card";
import { ConfirmDialog } from "@/components/ui/ConfirmDialog";
import { EmptyState } from "@/components/ui/EmptyState";
import { Icon } from "@/components/ui/Icon";
import { Input } from "@/components/ui/Input";
import { Pagination } from "@/components/ui/Pagination";
import { Table, type Column } from "@/components/ui/Table";
import { useToast } from "@/components/ui/Toast";
import { useAuth } from "@/features/auth/components/AuthProvider";
import { errorMessage } from "@/lib/api-client";
import { useDebouncedValue } from "@/lib/use-debounced-value";
import { useListParams } from "@/lib/use-list-params";
import { useDeleteFamille, useFamilles } from "../../hooks/useFamilles";
import type { Famille } from "../../types/famille.types";
import { FamilleFormModal } from "../FamilleFormModal";

export function FamillesView() {
  const { can } = useAuth();
  const toast = useToast();
  const canWrite = can("article.write");
  const list = useListParams({ search: "" }, "code,asc");
  const debouncedSearch = useDebouncedValue(list.filters.search);
  const query = useFamilles({ ...list.params, search: debouncedSearch });
  const remove = useDeleteFamille();
  const [editing, setEditing] = useState<Famille | null>(null);
  const [formOpen, setFormOpen] = useState(false);
  const [toDelete, setToDelete] = useState<Famille | null>(null);

  const columns: Column<Famille>[] = [
    { key: "code", header: "Code", sortKey: "code", render: (f) => <strong>{f.code}</strong>, width: "160px" },
    { key: "libelle", header: "Libellé", sortKey: "libelle", render: (f) => f.libelle },
    { key: "parent", header: "Famille parente", render: (f) => f.parentLibelle ?? "—" },
    {
      key: "actions",
      header: "",
      align: "right",
      render: (f) =>
        canWrite && (
          <Button size="sm" variant="ghost" onClick={(e) => { e.stopPropagation(); setToDelete(f); }} aria-label={`Supprimer ${f.code}`}>
            <Icon name="trash" size={15} />
          </Button>
        ),
    },
  ];

  const openForm = (famille: Famille | null) => {
    setEditing(famille);
    setFormOpen(true);
  };

  const confirmDelete = async () => {
    if (!toDelete) return;
    try {
      await remove.mutateAsync(toDelete.id);
      toast.success(`Famille ${toDelete.code} supprimée`);
    } catch (e) {
      toast.error(errorMessage(e));
    } finally {
      setToDelete(null);
    }
  };

  return (
    <>
      <PageHeader
        title="Familles d'articles"
        subtitle="Familles et sous-familles (deux niveaux)"
        actions={canWrite && <Button variant="primary" icon={<Icon name="plus" size={16} />} onClick={() => openForm(null)}>Nouvelle famille</Button>}
      />
      <Card padded={false}>
        <FilterBar>
          <FilterItem grow>
            <Input
              type="search"
              placeholder="Rechercher par code ou libellé"
              aria-label="Rechercher"
              value={list.filters.search}
              onChange={(e) => list.updateFilter("search", e.target.value)}
            />
          </FilterItem>
        </FilterBar>
        <Table
          columns={columns}
          rows={query.data?.content}
          rowKey={(f) => f.id}
          loading={query.isLoading}
          error={query.error}
          onRetry={() => query.refetch()}
          sort={list.sort}
          onSort={list.toggleSort}
          onRowClick={canWrite ? (f) => openForm(f) : undefined}
          empty={
            <EmptyState
              title="Aucune famille"
              description="Créez des familles pour classer vos articles."
              action={canWrite && <Button onClick={() => openForm(null)}>Créer une famille</Button>}
            />
          }
          footer={
            query.data && (
              <Pagination
                page={query.data.page}
                totalPages={query.data.totalPages}
                totalElements={query.data.totalElements}
                size={list.size}
                onPageChange={list.setPage}
                onSizeChange={list.setSize}
              />
            )
          }
        />
      </Card>
      <FamilleFormModal open={formOpen} famille={editing} onClose={() => setFormOpen(false)} />
      <ConfirmDialog
        open={!!toDelete}
        title="Supprimer la famille"
        message={`Supprimer la famille « ${toDelete?.libelle ?? ""} » ? Impossible si elle contient des articles ou des sous-familles.`}
        confirmLabel="Supprimer la famille"
        danger
        loading={remove.isPending}
        onConfirm={confirmDelete}
        onCancel={() => setToDelete(null)}
      />
    </>
  );
}
