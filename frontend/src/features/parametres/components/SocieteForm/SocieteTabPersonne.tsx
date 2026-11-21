"use client";

import type { UseFormReturn } from "react-hook-form";
import { FormGrid } from "@/components/layout/FormGrid";
import { TextField } from "@/components/ui/FormControls";
import type { SocieteFormValues } from "../../schemas/societe.schema";

/** Owner identity when the company is a "personne physique". */
export function SocieteTabPersonne({ form }: { form: UseFormReturn<SocieteFormValues> }) {
  return (
    <FormGrid columns={2}>
      <TextField form={form} name="ppNom" label="Nom" />
      <TextField form={form} name="ppPrenom" label="Prénom" />
      <TextField form={form} name="ppCin" label="CIN" />
      <TextField form={form} name="ppQualite" label="Qualité" hint="Gérant, propriétaire…" />
    </FormGrid>
  );
}
