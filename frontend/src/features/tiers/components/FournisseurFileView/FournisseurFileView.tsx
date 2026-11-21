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
import { TiersSoldePanel } from "@/features/reglements/components/TiersSoldePanel";
import { errorMessage } from "@/lib/api-client";
import { useDeleteTiers, useFournisseur } from "../../hooks/useTiers";
import { FournisseurForm, type FournisseurFormSection } from "../FournisseurForm";
import { SituationFiscaleBadge } from "../SituationFiscaleBadge";
import styles from "./FournisseurFileView.module.css";

type Tab = FournisseurFormSection | "situation";

/** "Fichier fournisseur": information générale, informations légales, attestation fiscale, situation. */
export function FournisseurFileView({ id }: { id?: number }) {
  const { can } = useAuth();
  const router = useRouter();
  const toast = useToast();
  const query = useFournisseur(id);
  const remove = useDeleteTiers("fournisseurs");
  const [tab, setTab] = useState<Tab>("general");
  const [confirm, setConfirm] = useState(false);
  const canWrite = can("fournisseur.write");

  if (id && query.isLoading) return <Skeleton height={360} />;
  if (id && (query.error || !query.data)) return <ErrorState error={query.error} onRetry={() => query.refetch()} />;
  const f = query.data;

  const tabs: { key: Tab; label: string }[] = [
    { key: "general", label: "Information générale" },
    { key: "legal", label: "Informations légales" },
    { key: "attestation", label: "Attestation de régularité fiscale" },
    ...(f && can("reglement.read") ? [{ key: "situation" as Tab, label: "Situation fournisseur" }] : []),
  ];

  const onDelete = async () => {
    if (!f) return;
    try {
      await remove.mutateAsync(f.id);
      toast.success(`${f.raisonSociale} supprimé`);
      router.replace("/fournisseurs");
    } catch (e) {
      toast.error(errorMessage(e));
      setConfirm(false);
    }
  };

  return (
    <>
      <PageHeader
        title={f ? f.raisonSociale : "Nouveau fournisseur"}
        breadcrumbs={[{ label: "Fournisseurs", href: "/fournisseurs" }, { label: f?.code ?? "Nouveau" }]}
        subtitle={f && (
          <>
            {f.code}
            <SituationFiscaleBadge reguliere={f.situationFiscaleReguliere} />
            {!f.actif && <Badge>Désactivé</Badge>}
          </>
        )}
        actions={f && canWrite && <Button variant="ghost" onClick={() => setConfirm(true)}>Supprimer</Button>}
      />
      <div className={styles.tabs}>
        <Tabs items={tabs} active={tab} onChange={setTab} />
      </div>
      <div hidden={tab === "situation"}>
        <FournisseurForm fournisseur={f} section={tab === "situation" ? "general" : tab} readOnly={!canWrite} />
      </div>
      {f && tab === "situation" && <TiersSoldePanel kind="fournisseurs" tiersId={f.id} />}
      <ConfirmDialog
        open={confirm}
        title="Supprimer le fournisseur"
        message={`${f?.raisonSociale ?? ""} sera désactivé et masqué des listes. Ses documents sont conservés.`}
        confirmLabel="Supprimer le fournisseur"
        danger
        loading={remove.isPending}
        onConfirm={onDelete}
        onCancel={() => setConfirm(false)}
      />
    </>
  );
}
