"use client";

import Link from "next/link";
import { useRouter } from "next/navigation";
import { useState } from "react";
import { PageHeader } from "@/components/layout/PageHeader";
import { Button } from "@/components/ui/Button";
import { Card } from "@/components/ui/Card";
import { ConfirmDialog } from "@/components/ui/ConfirmDialog";
import { ErrorState } from "@/components/ui/ErrorState";
import { Skeleton } from "@/components/ui/Skeleton";
import { useToast } from "@/components/ui/Toast";
import { useAuth } from "@/features/auth/components/AuthProvider";
import { routeOf } from "@/features/documents/config/documentTypes";
import { errorMessage } from "@/lib/api-client";
import { formatDate, formatMoney } from "@/lib/format";
import { TYPE_REGLEMENT_LABELS } from "@/lib/types";
import { useAffecterReglement, useChangerStatutReglement, useDeleteReglement, useReglement, useSoldeTiers } from "../../hooks/useReglements";
import { STATUT_REGLEMENT_LABELS, TRANSITIONS, type Sens, type StatutReglement } from "../../types/reglement.types";
import { AllocationTable, toAffectations } from "../AllocationTable";
import { ReglementStatusBadge } from "../ReglementStatusBadge";
import styles from "./ReglementDetailView.module.css";

export function ReglementDetailView({ sens, id }: { sens: Sens; id: number }) {
  const router = useRouter();
  const toast = useToast();
  const { can } = useAuth();
  const query = useReglement(sens, id);
  const statut = useChangerStatutReglement(sens, id);
  const affecter = useAffecterReglement(sens, id);
  const remove = useDeleteReglement(sens);
  const solde = useSoldeTiers(sens, query.data?.tiersId ?? null);
  const [pendingStatut, setPendingStatut] = useState<StatutReglement | null>(null);
  const [confirmDelete, setConfirmDelete] = useState(false);
  const [allocation, setAllocation] = useState<Record<number, string>>({});

  if (query.isLoading) return <Skeleton height={300} />;
  if (query.error || !query.data) return <ErrorState error={query.error} onRetry={() => query.refetch()} />;
  const r = query.data;
  const canWrite = can("reglement.write");
  const factureType = sens === "clients" ? "FACTURE" : "FACTURE_FOURNISSEUR";

  const changeStatut = async () => {
    if (!pendingStatut) return;
    try {
      await statut.mutateAsync(pendingStatut);
      toast.success(`Statut : ${STATUT_REGLEMENT_LABELS[pendingStatut]}`);
    } catch (e) {
      toast.error(errorMessage(e));
    } finally {
      setPendingStatut(null);
    }
  };

  const onAffecter = async () => {
    try {
      await affecter.mutateAsync(toAffectations(allocation));
      setAllocation({});
      toast.success("Affectation enregistrée");
    } catch (e) {
      toast.error(errorMessage(e));
    }
  };

  const onDelete = async () => {
    try {
      await remove.mutateAsync(r.id);
      toast.success(`Règlement ${r.numero} supprimé`);
      router.replace(`/reglements/${sens}`);
    } catch (e) {
      toast.error(errorMessage(e));
      setConfirmDelete(false);
    }
  };

  return (
    <>
      <PageHeader
        title={`Règlement ${r.numero}`}
        breadcrumbs={[{ label: sens === "clients" ? "Règlements clients" : "Règlements fournisseurs", href: `/reglements/${sens}` }, { label: r.numero }]}
        subtitle={<><ReglementStatusBadge statut={r.statut} /> {r.tiersNom}</>}
        actions={canWrite && (
          <>
            {TRANSITIONS[r.statut].map((s) => (
              <Button key={s} variant={s === "IMPAYE" ? "ghost" : "secondary"} onClick={() => setPendingStatut(s)}>
                {s === "REMIS_EN_BANQUE" ? "Remettre en banque" : s === "ENCAISSE" ? "Marquer encaissé" : "Déclarer impayé"}
              </Button>
            ))}
            <Button variant="ghost" onClick={() => setConfirmDelete(true)}>Supprimer</Button>
          </>
        )}
      />
      <div className={styles.stack}>
        <Card>
          <dl className={styles.grid}>
            <div><dt>Tiers</dt><dd><Link href={`/${sens}/${r.tiersId}`}>{r.tiersNom}</Link></dd></div>
            <div><dt>Date</dt><dd>{formatDate(r.dateReglement)}</dd></div>
            <div><dt>Mode</dt><dd>{TYPE_REGLEMENT_LABELS[r.mode]}</dd></div>
            <div><dt>Montant</dt><dd className={styles.strong}>{formatMoney(r.montant, "MAD")}</dd></div>
            <div><dt>Référence</dt><dd>{r.reference ?? "—"}</dd></div>
            <div><dt>Banque</dt><dd>{r.banque ?? "—"}</dd></div>
            <div><dt>Échéance</dt><dd>{formatDate(r.dateEcheance)}</dd></div>
            <div><dt>Non affecté</dt><dd>{formatMoney(r.montantNonAffecte)}</dd></div>
            {r.notes && <div className={styles.full}><dt>Notes</dt><dd>{r.notes}</dd></div>}
          </dl>
          {r.statut === "IMPAYE" && (
            <p className={styles.warning}>Impayé : les montants affectés ont été réintégrés dans le reste à payer des factures.</p>
          )}
        </Card>
        <Card title="Factures réglées">
          <ul className={styles.list}>
            {r.affectations.map((a) => (
              <li key={a.id}>
                <Link href={routeOf(factureType, a.documentId)}>{a.documentNumero}</Link>
                <span className="num">{formatMoney(a.montant)}</span>
              </li>
            ))}
            {r.affectations.length === 0 && <li className={styles.muted}>Aucune affectation (acompte)</li>}
          </ul>
        </Card>
        {canWrite && r.statut !== "IMPAYE" && Number(r.montantNonAffecte) > 0 && (
          <Card title="Affecter le montant restant" actions={
            <Button variant="primary" size="sm" onClick={onAffecter} loading={affecter.isPending}
              disabled={toAffectations(allocation).length === 0}>Enregistrer l&apos;affectation</Button>
          }>
            <AllocationTable factures={solde.data?.facturesNonSoldees ?? []} values={allocation} onChange={setAllocation}
              disponible={Number(r.montantNonAffecte)} />
          </Card>
        )}
      </div>
      <ConfirmDialog
        open={!!pendingStatut}
        title="Changer le statut"
        message={pendingStatut === "IMPAYE"
          ? "Le règlement sera déclaré impayé : les montants affectés reviennent dans le reste à payer des factures. Cette action est définitive."
          : `Passer le règlement au statut « ${pendingStatut ? STATUT_REGLEMENT_LABELS[pendingStatut] : ""} » ?`}
        confirmLabel="Confirmer"
        danger={pendingStatut === "IMPAYE"}
        loading={statut.isPending}
        onConfirm={changeStatut}
        onCancel={() => setPendingStatut(null)}
      />
      <ConfirmDialog
        open={confirmDelete}
        title="Supprimer le règlement"
        message="Le règlement sera supprimé et ses affectations annulées (le reste à payer des factures est rétabli)."
        confirmLabel="Supprimer le règlement"
        danger
        loading={remove.isPending}
        onConfirm={onDelete}
        onCancel={() => setConfirmDelete(false)}
      />
    </>
  );
}
