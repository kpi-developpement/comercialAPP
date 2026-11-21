"use client";

import { PageHeader } from "@/components/layout/PageHeader";
import { Card } from "@/components/ui/Card";
import { ErrorState } from "@/components/ui/ErrorState";
import { Skeleton } from "@/components/ui/Skeleton";
import { RequireAuth } from "@/features/auth/components/RequireAuth";
import { useAuth } from "@/features/auth/components/AuthProvider";
import { formatDate } from "@/lib/format";
import { DOCUMENT_TYPES, routeOf } from "../../config/documentTypes";
import { useDocument } from "../../hooks/useDocuments";
import type { TypeDocument } from "../../types/document.types";
import { DocumentActions } from "../DocumentActions";
import { DocumentEditor } from "../DocumentEditor";
import { DocumentInfo } from "../DocumentInfo";
import { DocumentLinesReadonly } from "../DocumentLinesReadonly";
import { DocumentStatusBadge } from "../DocumentStatusBadge";
import { DocumentTotals } from "../DocumentTotals";
import styles from "./DocumentDetailView.module.css";

/** Draft → editable grid; validated → read-only view with transformation actions. */
export function DocumentDetailView({ type, id }: { type: TypeDocument; id: number }) {
  const config = DOCUMENT_TYPES[type];
  const { can } = useAuth();
  const query = useDocument(type, id);

  if (query.isLoading) return <Skeleton height={400} />;
  if (query.error || !query.data) return <ErrorState error={query.error} onRetry={() => query.refetch()} />;
  const doc = query.data;
  const editable = doc.statut === "BROUILLON" && can(config.writePermission);

  return (
    <RequireAuth permission={config.readPermission}>
      <PageHeader
        title={doc.numero ? `${config.label} ${doc.numero}` : `${config.label} (brouillon)`}
        breadcrumbs={[{ label: config.labelPlural, href: routeOf(type) }, { label: doc.numero ?? "Brouillon" }]}
        subtitle={<><DocumentStatusBadge statut={doc.statut} /> {doc.tiersNom} · {formatDate(doc.dateDocument)}</>}
        actions={<DocumentActions doc={doc} />}
      />
      {doc.warnings.length > 0 && (
        <ul className={styles.warnings} role="status">
          {doc.warnings.map((w) => <li key={w}>{w}</li>)}
        </ul>
      )}
      {editable ? (
        <DocumentEditor key={doc.version} type={type} document={doc} />
      ) : (
        <div className={styles.stack}>
          <Card><DocumentInfo doc={doc} config={config} /></Card>
          <Card title="Lignes" padded={false}>
            <DocumentLinesReadonly lignes={doc.lignes} showRestante={doc.statut !== "BROUILLON" && !config.isAvoir} />
            <DocumentTotals totalHt={doc.totalHt} totalRemise={doc.totalRemise} totalTva={doc.totalTva} totalTtc={doc.totalTtc}
              recap={doc.recapTva} montantEnLettres={doc.montantEnLettres} />
          </Card>
        </div>
      )}
    </RequireAuth>
  );
}
