"use client";

import { useRouter } from "next/navigation";
import { useState } from "react";
import { PageHeader } from "@/components/layout/PageHeader";
import { Badge } from "@/components/ui/Badge";
import { Button } from "@/components/ui/Button";
import { ConfirmDialog } from "@/components/ui/ConfirmDialog";
import { ErrorState } from "@/components/ui/ErrorState";
import { Skeleton } from "@/components/ui/Skeleton";
import { Tabs } from "@/components/ui/Tabs";
import { useToast } from "@/components/ui/Toast";
import { useAuth } from "@/features/auth/components/AuthProvider";
import { ImpayesHistoryPanel } from "@/features/reglements/components/ImpayesHistoryPanel";
import { TiersSoldePanel } from "@/features/reglements/components/TiersSoldePanel";
import { errorMessage } from "@/lib/api-client";
import { useClient, useDeleteTiers } from "../../hooks/useTiers";
import { RISQUE_LABELS } from "../../types/tiers.types";
import { ClientForm, type ClientFormSection } from "../ClientForm";
import { ClientProduitsPanel } from "../ClientProduitsPanel";
import styles from "./ClientFileView.module.css";

type Tab = ClientFormSection | "produits" | "situation" | "impayes";

/** "Fichier client" with its five tabs; creation only shows the two form tabs. */
export function ClientFileView({ id }: { id?: number }) {
  const { can } = useAuth();
  const router = useRouter();
  const toast = useToast();
  const query = useClient(id);
  const remove = useDeleteTiers("clients");
  const [tab, setTab] = useState<Tab>("fiche");
  const [confirm, setConfirm] = useState(false);
  const canWrite = can("client.write");

  if (id && query.isLoading) return <Skeleton height={360} />;
  if (id && (query.error || !query.data)) return <ErrorState error={query.error} onRetry={() => query.refetch()} />;
  const client = query.data;

  const tabs: { key: Tab; label: string }[] = [
    { key: "fiche", label: "Fiche client" },
    { key: "autres", label: "Autres informations" },
    ...(client
      ? ([
          { key: "produits", label: "Produits vendus par client" },
          ...(can("reglement.read") ? [{ key: "situation", label: "Situation client" }, { key: "impayes", label: "Historique des impayés" }] : []),
        ] as { key: Tab; label: string }[])
      : []),
  ];

  const onDelete = async () => {
    if (!client) return;
    try {
      await remove.mutateAsync(client.id);
      toast.success(`${client.raisonSociale} supprimé`);
      router.replace("/clients");
    } catch (e) {
      toast.error(errorMessage(e));
      setConfirm(false);
    }
  };

  return (
    <>
      <PageHeader
        title={client ? client.raisonSociale : "Nouveau client"}
        breadcrumbs={[{ label: "Clients", href: "/clients" }, { label: client?.code ?? "Nouveau" }]}
        subtitle={client && (
          <>
            {client.code}
            {client.prospect ? <Badge tone="info">Prospect</Badge> : <Badge tone="primary">Client</Badge>}
            {client.bloque && <Badge tone="danger">Compte bloqué</Badge>}
            {client.risque && client.risque !== "NORMAL" &&<Badge tone="warning">{RISQUE_LABELS[client.risque]}</Badge>}
            {!client.actif && <Badge>Inactif</Badge>}
          </>
        )}
        actions={client && canWrite && <Button variant="ghost" onClick={() => setConfirm(true)}>Supprimer</Button>}
      />
      <div className={styles.tabs}>
        <Tabs items={tabs} active={tab} onChange={setTab} />
      </div>
      <div hidden={tab !== "fiche" && tab !== "autres"}>
        <ClientForm client={client} section={tab === "autres" ? "autres" : "fiche"} readOnly={!canWrite} />
      </div>
      {client && tab === "produits" && <ClientProduitsPanel clientId={client.id} canWrite={canWrite} />}
      {client && tab === "situation" && <TiersSoldePanel kind="clients" tiersId={client.id} />}
      {client && tab === "impayes" && <ImpayesHistoryPanel sens="clients" tiersId={client.id} />}
      <ConfirmDialog
        open={confirm}
        title="Supprimer le client"
        message={`${client?.raisonSociale ?? ""} sera désactivé et masqué des listes. Ses documents sont conservés.`}
        confirmLabel="Supprimer le client"
        danger
        loading={remove.isPending}
        onConfirm={onDelete}
        onCancel={() => setConfirm(false)}
      />
    </>
  );
}
