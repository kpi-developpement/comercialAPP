"use client";

import type { UseFormReturn } from "react-hook-form";
import { FormGrid } from "@/components/layout/FormGrid";
import { FormSection } from "@/components/layout/FormSection";
import { CheckboxField, NumberField, SelectField, toOptions } from "@/components/ui/FormControls";
import { DEVISES } from "@/lib/form";
import type { SocieteFormValues } from "../../schemas/societe.schema";

export function SocieteTabAutres({ form }: { form: UseFormReturn<SocieteFormValues> }) {
  return (
    <>
      <FormSection title="Société">
        <FormGrid columns={3}>
          <SelectField form={form} name="devise" label="Devise de tenue" required options={toOptions(DEVISES)} />
          <NumberField form={form} name="capital" label="Capital (MAD)" />
          <NumberField form={form} name="effectif" label="Effectif" step="1" />
        </FormGrid>
      </FormSection>
      <FormSection title="Gestion du stock">
        <CheckboxField form={form} name="autoriserStockNegatif" label="Autoriser le stock négatif (ne pas bloquer les sorties)" />
      </FormSection>
    </>
  );
}
