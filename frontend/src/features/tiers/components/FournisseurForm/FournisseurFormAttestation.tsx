"use client";

import type { UseFormReturn } from "react-hook-form";
import { FormGrid } from "@/components/layout/FormGrid";
import { DateField, TextField } from "@/components/ui/FormControls";
import { todayIso } from "@/lib/format";
import type { FournisseurFormValues } from "../../schemas/fournisseur.schema";
import { SituationFiscaleBadge } from "../SituationFiscaleBadge";
import styles from "./FournisseurForm.module.css";

/** Attestation de régularité fiscale: the tax situation is regular while the attestation is valid. */
export function FournisseurFormAttestation({ form }: { form: UseFormReturn<FournisseurFormValues> }) {
  const validite = form.watch("attestationValidite");
  const reguliere = !!validite && validite >= todayIso();
  return (
    <>
      <div className={styles.attestationStatus}>
        <SituationFiscaleBadge reguliere={reguliere} large />
        <p className={styles.hint}>
          La situation est régulière tant que la date de validité de l&apos;attestation n&apos;est pas dépassée.
        </p>
      </div>
      <FormGrid columns={3}>
        <TextField form={form} name="attestationNumero" label="N° de l'attestation" />
        <DateField form={form} name="attestationDate" label="Date de l'attestation" />
        <DateField form={form} name="attestationValidite" label="Valable jusqu'au" />
      </FormGrid>
    </>
  );
}
