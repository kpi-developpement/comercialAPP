"use client";

import { useState } from "react";
import { FilterBar, FilterItem } from "@/components/layout/FilterBar";
import { PageHeader } from "@/components/layout/PageHeader";
import { Badge } from "@/components/ui/Badge";
import { Button } from "@/components/ui/Button";
import { Card } from "@/components/ui/Card";
import { Icon } from "@/components/ui/Icon";
import { Input } from "@/components/ui/Input";
import { Pagination } from "@/components/ui/Pagination";
import { Select } from "@/components/ui/Select";
import { Table, type Column } from "@/components/ui/Table";
import { RequireAuth } from "@/features/auth/components/RequireAuth";
import { formatDate } from "@/lib/format";
import { ROLE_LABELS, type Role } from "@/lib/permissions";
import { useDebouncedValue } from "@/lib/use-debounced-value";
import { useListParams } from "@/lib/use-list-params";
import { useUtilisateurs } from "../../hooks/useUtilisateurs";
import type { Utilisateur } from "../../types/utilisateur.types";
import { ResetPasswordModal } from "../ResetPasswordModal";
import { UtilisateurFormModal } from "../UtilisateurFormModal";

const ROLES = (Object.keys(ROLE_LABELS) as Role[]).map((r) => ({ value: r, label: ROLE_LABELS[r] }));

export function UtilisateursView() {
  const list = useListParams({ search: "", role: "", actif: "" }, "username,asc");
  const search = useDebouncedValue(list.filters.search);
  const query = useUtilisateurs({ ...list.params, search });
  const [editing, setEditing] = useState<Utilisateur | null>(null);
  const [formOpen, setFormOpen] = useState(false);
  const [resetting, setResetting] = useState<Utilisateur | null>(null);

  const columns: Column<Utilisateur>[] = [
    { key: "username", header: "Identifiant", sortKey: "username", render: (u) => <strong>{u.username}</strong> },
    { key: "nom", header: "Nom complet", sortKey: "nomComplet", render: (u) => u.nomComplet },
    { key: "email", header: "E-mail", render: (u) => u.email ?? "—" },
    { key: "role", header: "Rôle", sortKey: "role", render: (u) => <Badge tone="primary">{ROLE_LABELS[u.role]}</Badge> },
    { key: "actif", header: "Statut", render: (u) => (u.actif ? <Badge tone="success">Actif</Badge> : <Badge>Désactivé</Badge>) },
    { key: "created", header: "Créé le", render: (u) => formatDate(u.createdAt) },
    {
      key: "actions",
      header: "",
      align: "right",
      render: (u) => (
        <Button size="sm" variant="ghost" onClick={(e) => { e.stopPropagation(); setResetting(u); }}>Mot de passe</Button>
      ),
    },
  ];

  return (
    <RequireAuth permission="admin">
      <PageHeader
        title="Utilisateurs"
        subtitle="Comptes et rôles : administrateur, commercial, magasinier, comptable"
        actions={<Button variant="primary" icon={<Icon name="plus" size={16} />} onClick={() => { setEditing(null); setFormOpen(true); }}>Nouvel utilisateur</Button>}
      />
      <Card padded={false}>
        <FilterBar>
          <FilterItem grow>
            <Input type="search" aria-label="Rechercher" placeholder="Identifiant, nom ou e-mail" value={list.filters.search}
              onChange={(e) => list.updateFilter("search", e.target.value)} />
          </FilterItem>
          <FilterItem>
            <Select aria-label="Rôle" options={ROLES} placeholder="Tous les rôles" value={list.filters.role}
              onChange={(e) => list.updateFilter("role", e.target.value)} />
          </FilterItem>
          <FilterItem>
            <Select aria-label="Statut" placeholder="Tous" options={[{ value: "true", label: "Actifs" }, { value: "false", label: "Désactivés" }]}
              value={list.filters.actif} onChange={(e) => list.updateFilter("actif", e.target.value)} />
          </FilterItem>
        </FilterBar>
        <Table
          columns={columns}
          rows={query.data?.content}
          rowKey={(u) => u.id}
          loading={query.isLoading}
          error={query.error}
          onRetry={() => query.refetch()}
          sort={list.sort}
          onSort={list.toggleSort}
          onRowClick={(u) => { setEditing(u); setFormOpen(true); }}
          footer={query.data && (
            <Pagination page={query.data.page} totalPages={query.data.totalPages} totalElements={query.data.totalElements}
              size={list.size} onPageChange={list.setPage} onSizeChange={list.setSize} />
          )}
        />
      </Card>
      <UtilisateurFormModal open={formOpen} utilisateur={editing} onClose={() => setFormOpen(false)} />
      <ResetPasswordModal utilisateur={resetting} onClose={() => setResetting(null)} />
    </RequireAuth>
  );
}
