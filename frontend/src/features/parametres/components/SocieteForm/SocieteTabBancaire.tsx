"use client";

import type { UseFormReturn } from "react-hook-form";
import { FormGrid } from "@/components/layout/FormGrid";
import { TextField } from "@/components/ui/FormControls";
import type { SocieteFormValues } from "../../schemas/societe.schema";

/** Bank details printed in the footer of documents. */
export function SocieteTabBancaire({ form }: { form: UseFormReturn<SocieteFormValues> }) {
  return (
    <FormGrid columns={2}>
      <TextField form={form} name="banque" label="Banque" />
      <TextField form={form} name="agenceBancaire" label="Agence" />
      <TextField form={form} name="rib" label="R.I.B" hint="24 chiffres" />
      <TextField form={form} name="swift" label="Swift / BIC" />
    </FormGrid>
  );
}
