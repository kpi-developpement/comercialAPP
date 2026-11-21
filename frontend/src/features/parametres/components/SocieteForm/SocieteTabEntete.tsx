"use client";

import type { UseFormReturn } from "react-hook-form";
import { FormGrid, FormGridFull } from "@/components/layout/FormGrid";
import { TextAreaField } from "@/components/ui/FormControls";
import type { SocieteFormValues } from "../../schemas/societe.schema";
import { LogoField } from "./LogoField";

/** Logo, header text under the company name and footer of printed documents. */
export function SocieteTabEntete({ form }: { form: UseFormReturn<SocieteFormValues> }) {
  return (
    <FormGrid>
      <FormGridFull>
        <LogoField value={form.watch("logo")} onChange={(v) => form.setValue("logo", v, { shouldDirty: true })} />
      </FormGridFull>
      <TextAreaField form={form} name="entete" label="Texte d'entête" hint="Imprimé sous le nom de la société (slogan, activité…)" />
      <TextAreaField form={form} name="piedDePage" label="Pied de page des documents" />
    </FormGrid>
  );
}
