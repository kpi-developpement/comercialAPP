"use client";

import Link from "next/link";
import { useState } from "react";
import { PageHeader } from "@/components/layout/PageHeader";
import { Button } from "@/components/ui/Button";
import { Card } from "@/components/ui/Card";
import { EmptyState } from "@/components/ui/EmptyState";
import { Icon } from "@/components/ui/Icon";
import { Pagination } from "@/components/ui/Pagination";
import { RequireAuth } from "@/features/auth/components/RequireAuth";
import { useAuth } from "@/features/auth/components/AuthProvider";
import { useDebouncedValue } from "@/lib/use-debounced-value";
import { useListParams } from "@/lib/use-list-params";
import { DOCUMENT_TYPES, routeOf } from "../../config/documentTypes";
import { useDocuments } from "../../hooks/useDocuments";
import type { DocumentFilters, TypeDocument } from "../../types/document.types";
import { DocumentsFilters } from "../DocumentsFilters";
import { DocumentsTable } from "../DocumentsTable";
import { GroupDialog } from "../GroupDialog";

const INITIAL: DocumentFilters = { search: "", statut: "", statutPaiement: "", dateDebut: "", dateFin: "" };

/** List page shared by every sales / purchase document type. */
export function DocumentListView({ type }: { type: TypeDocument }) {
  const config = DOCUMENT_TYPES[type];
  const { can } = useAuth();
  const list = useListParams<DocumentFilters>(INITIAL, "dateDocument,desc");
  const search = useDebouncedValue(list.filters.search);
  const query = useDocuments(type, { ...list.params, search });
  const [grouping, setGrouping] = useState(false);
  const canWrite = can(config.writePermission);
  const hasFilters = Object.values(list.filters).some((v) => v !== "");

  return (
    <RequireAuth permission={config.readPermission}>
      <PageHeader
        title={config.labelPlural}
        subtitle={query.data ? `${query.data.totalElements} document(s)` : undefined}
        actions={canWrite && (
          <>
            {config.groupFrom && (
              <Button onClick={() => setGrouping(true)}>
                Regrouper des {DOCUMENT_TYPES[config.groupFrom].labelPlural.toLowerCase()}
              </Button>
            )}
            <Link href={`${routeOf(type)}/nouveau`}>
              <Button variant="primary" icon={<Icon name="plus" size={16} />}>{config.nouveau}</Button>
            </Link>
          </>
        )}
      />
      <Card padded={false}>
        <DocumentsFilters config={config} filters={list.filters} onChange={list.updateFilter} />
        <DocumentsTable
          config={config}
          rows={query.data?.content}
          loading={query.isLoading}
          error={query.error}
          onRetry={() => query.refetch()}
          sort={list.sort}
          onSort={list.toggleSort}
          empty={hasFilters
            ? <EmptyState title="Aucun document ne correspond aux filtres" action={<Button onClick={() => list.setFilters(INITIAL)}>Effacer les filtres</Button>} />
            : <EmptyState title={`Aucun document`} description={`Créez un document ou transformez un document existant.`} />}
          footer={query.data && (
            <Pagination page={query.data.page} totalPages={query.data.totalPages} totalElements={query.data.totalElements}
              size={list.size} onPageChange={list.setPage} onSizeChange={list.setSize} />
          )}
        />
      </Card>
      {config.groupFrom && <GroupDialog open={grouping} config={config} onClose={() => setGrouping(false)} />}
    </RequireAuth>
  );
}
