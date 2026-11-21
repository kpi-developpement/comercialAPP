"use client";

import type { UseFormReturn } from "react-hook-form";
import { FormGrid } from "@/components/layout/FormGrid";
import { CheckboxField, NumberField, SelectField, TextField, toOptions } from "@/components/ui/FormControls";
import { FORMES_JURIDIQUES } from "@/lib/form";
import type { SocieteFormValues } from "../../schemas/societe.schema";

export function SocieteTabJuridique({ form }: { form: UseFormReturn<SocieteFormValues> }) {
  return (
    <FormGrid columns={3}>
      <TextField form={form} name="patente" label="Taxe professionnelle" />
      <TextField form={form} name="identifiantFiscal" label="Identifiant fiscal" />
      <TextField form={form} name="subdivision" label="Subdivision" />
      <TextField form={form} name="rc" label="R.C" />
      <TextField form={form} name="villeRc" label="Ville R.C" />
      <SelectField form={form} name="formeJuridique" label="Forme juridique" options={toOptions(FORMES_JURIDIQUES)} placeholder="Non définie" />
      <TextField form={form} name="cnss" label="C.N.S.S" />
      <TextField form={form} name="cnss2" label="C.N.S.S 2ème affiliation" />
      <TextField form={form} name="rafReception" label="RAF / Réception" />
      <TextField form={form} name="activite" label="Activité" />
      <NumberField form={form} name="tauxCm" label="Taux de la C.M (%)" />
      <NumberField form={form} name="tauxImpot" label="Taux de l'impôt (%)" />
      <CheckboxField form={form} name="soumisImpot" label="Soumis à l'impôt" />
    </FormGrid>
  );
}
