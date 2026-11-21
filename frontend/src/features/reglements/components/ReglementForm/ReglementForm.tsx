"use client";

import { zodResolver } from "@hookform/resolvers/zod";
import { useRouter, useSearchParams } from "next/navigation";
import { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { Button } from "@/components/ui/Button";
import { Card } from "@/components/ui/Card";
import { useToast } from "@/components/ui/Toast";
import { useTiers } from "@/features/tiers/hooks/useTiers";
import { errorMessage } from "@/lib/api-client";
import { todayIso } from "@/lib/format";
import { useCreateReglement, useSoldeTiers } from "../../hooks/useReglements";
import { reglementSchema, type ReglementFormValues } from "../../schemas/reglement.schema";
import type { Sens } from "../../types/reglement.types";
import { AllocationTable, toAffectations } from "../AllocationTable";
import { ReglementFormFields } from "./ReglementFormFields";
import styles from "./ReglementForm.module.css";

/** New payment: amount + instrument, then allocation to unpaid invoices. */
export function ReglementForm({ sens }: { sens: Sens }) {
  const router = useRouter();
  const toast = useToast();
  const params = useSearchParams();
  const initialTiers = params.get("tiersId") ? Number(params.get("tiersId")) : null;
  const factureId = params.get("factureId") ? Number(params.get("factureId")) : null;
  const create = useCreateReglement(sens);
  const prefill = useTiers(sens, initialTiers);
  const form = useForm<ReglementFormValues>({
    resolver: zodResolver(reglementSchema),
    mode: "onBlur",
    defaultValues: {
      tiersId: initialTiers, tiersNom: "", dateReglement: todayIso(), mode: "VIREMENT", montant: 0,
      reference: "", banque: "", dateEcheance: "", notes: "",
    },
  });
  const tiersId = form.watch("tiersId");
  const montant = Number(form.watch("montant")) || 0;
  const solde = useSoldeTiers(sens, tiersId);
  const factures = solde.data?.facturesNonSoldees ?? [];
  const [allocation, setAllocation] = useState<Record<number, string>>({});

  useEffect(() => {
    if (prefill.data) {
      form.setValue("tiersNom", prefill.data.raisonSociale);
      if (prefill.data.modeReglementDefaut) form.setValue("mode", prefill.data.modeReglementDefaut);
    }
  }, [prefill.data, form]);

  useEffect(() => {
    if (!factureId || !solde.data) return;
    const f = solde.data.facturesNonSoldees.find((x) => x.id === factureId);
    if (f) {
      form.setValue("montant", Number(f.resteAPayer));
      setAllocation({ [f.id]: String(Number(f.resteAPayer)) });
    }
  }, [factureId, solde.data, form]);

  const onSubmit = async (v: ReglementFormValues) => {
    const affectations = toAffectations(allocation);
    const total = affectations.reduce((s, a) => s + a.montant, 0);
    if (total > v.montant + 0.001) {
      toast.error("Le total affecté dépasse le montant du règlement");
      return;
    }
    const titre = v.mode === "CHEQUE" || v.mode === "EFFET";
    try {
      const created = await create.mutateAsync({
        tiersId: v.tiersId!,
        dateReglement: v.dateReglement,
        mode: v.mode,
        montant: v.montant,
        reference: v.reference.trim() || null,
        banque: v.banque.trim() || null,
        dateEcheance: titre && v.dateEcheance ? v.dateEcheance : null,
        notes: v.notes.trim() || null,
        affectations,
      });
      toast.success(`Règlement ${created.numero} enregistré`);
      router.replace(`/reglements/${sens}/${created.id}`);
    } catch (e) {
      toast.error(errorMessage(e));
    }
  };

  return (
    <form className={styles.form} onSubmit={form.handleSubmit(onSubmit)} noValidate>
      <Card title="Règlement">
        <ReglementFormFields form={form} sens={sens} onTiersChange={() => setAllocation({})} />
      </Card>
      <Card title="Affectation aux factures">
        {tiersId ? (
          <AllocationTable factures={factures} values={allocation} onChange={setAllocation} disponible={montant} />
        ) : (
          <p className={styles.hint}>Choisissez d&apos;abord le tiers.</p>
        )}
      </Card>
      <div className={styles.actions}>
        <Button variant="ghost" onClick={() => router.push(`/reglements/${sens}`)}>Retour à la liste</Button>
        <Button type="submit" variant="primary" loading={create.isPending}>Enregistrer le règlement</Button>
      </div>
    </form>
  );
}
