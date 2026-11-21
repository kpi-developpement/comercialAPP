"use client";

import { useRouter } from "next/navigation";
import { useState } from "react";
import { PageHeader } from "@/components/layout/PageHeader";
import { Button } from "@/components/ui/Button";
import { Card } from "@/components/ui/Card";
import { ConfirmDialog } from "@/components/ui/ConfirmDialog";
import { ErrorState } from "@/components/ui/ErrorState";
import { Skeleton } from "@/components/ui/Skeleton";
import { Table } from "@/components/ui/Table";
import { useToast } from "@/components/ui/Toast";
import { useAuth } from "@/features/auth/components/AuthProvider";
import { errorMessage } from "@/lib/api-client";
import { formatDate, formatQty } from "@/lib/format";
import { useDeleteTransfert, useTransfert } from "../../hooks/useTransferts";
import { OperationStatusBadge } from "../OperationStatusBadge";
import { TransfertForm } from "../TransfertForm";

export function TransfertDetailView({ id }: { id: number }) {
  const router = useRouter();
  const toast = useToast();
  const { can } = useAuth();
  const query = useTransfert(id);
  const remove = useDeleteTransfert();
  const [confirm, setConfirm] = useState(false);

  if (query.isLoading) return <Skeleton height={300} />;
  if (query.error || !query.data) return <ErrorState error={query.error} onRetry={() => query.refetch()} />;
  const t = query.data;
  const editable = t.statut === "BROUILLON" && can("stock.write");

  const onDelete = async () => {
    try {
      await remove.mutateAsync(t.id);
      toast.success("Transfert supprimé");
      router.replace("/stock/transferts");
    } catch (e) {
      toast.error(errorMessage(e));
      setConfirm(false);
    }
  };

  return (
    <>
      <PageHeader
        title={t.numero ? `Transfert ${t.numero}` : "Transfert (brouillon)"}
        breadcrumbs={[{ label: "Transferts", href: "/stock/transferts" }, { label: t.numero ?? "Brouillon" }]}
        subtitle={<><OperationStatusBadge statut={t.statut} /> {t.depotSourceLibelle} → {t.depotDestinationLibelle} · {formatDate(t.dateTransfert)}</>}
        actions={editable && <Button variant="ghost" onClick={() => setConfirm(true)}>Supprimer</Button>}
      />
      {editable ? (
        <TransfertForm key={t.id} transfert={t} />
      ) : (
        <Card title="Articles transférés" padded={false}>
          <Table
            columns={[
              { key: "code", header: "Code", render: (l) => <strong>{l.articleCode}</strong> },
              { key: "designation", header: "Désignation", render: (l) => l.designation },
              { key: "qte", header: "Quantité", align: "right", render: (l) => formatQty(l.quantite) },
            ]}
            rows={t.lignes}
            rowKey={(l) => l.id}
          />
        </Card>
      )}
      <ConfirmDialog open={confirm} title="Supprimer le transfert" message="Le brouillon de transfert sera supprimé."
        confirmLabel="Supprimer le transfert" danger loading={remove.isPending} onConfirm={onDelete} onCancel={() => setConfirm(false)} />
    </>
  );
}
