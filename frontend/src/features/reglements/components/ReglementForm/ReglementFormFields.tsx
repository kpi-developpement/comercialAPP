"use client";

import type { UseFormReturn } from "react-hook-form";
import { FormGrid, FormGridFull } from "@/components/layout/FormGrid";
import { FormField } from "@/components/ui/FormField";
import { Input } from "@/components/ui/Input";
import { Select } from "@/components/ui/Select";
import { Textarea } from "@/components/ui/Textarea";
import { useModesReglement } from "@/features/parametres/hooks/useParametres";
import { TiersPicker } from "@/features/tiers/components/TiersPicker";
import type { ReglementFormValues } from "../../schemas/reglement.schema";
import type { Sens } from "../../types/reglement.types";

interface Props {
  form: UseFormReturn<ReglementFormValues>;
  sens: Sens;
  onTiersChange: () => void;
}

export function ReglementFormFields({ form, sens, onTiersChange }: Props) {
  const { register, setValue, watch, formState: { errors } } = form;
  const { data: modes = [] } = useModesReglement();
  const mode = watch("mode");
  const titre = mode === "CHEQUE" || mode === "EFFET";

  return (
    <FormGrid columns={4}>
      <FormField label={sens === "clients" ? "Client" : "Fournisseur"} htmlFor="r-tiers" required error={errors.tiersId?.message}>
        <TiersPicker id="r-tiers" kind={sens} label={watch("tiersNom")} invalid={!!errors.tiersId}
          onSelect={(t) => {
            setValue("tiersId", t.id, { shouldValidate: true });
            setValue("tiersNom", t.raisonSociale);
            onTiersChange();
          }} />
      </FormField>
      <FormField label="Date" htmlFor="r-date" required error={errors.dateReglement?.message}>
        <Input id="r-date" type="date" {...register("dateReglement")} />
      </FormField>
      <FormField label="Mode" htmlFor="r-mode" required>
        <Select id="r-mode" options={modes.filter((m) => m.actif).map((m) => ({ value: m.code, label: m.libelle }))} {...register("mode")} />
      </FormField>
      <FormField label="Montant (MAD)" htmlFor="r-montant" required error={errors.montant?.message}>
        <Input id="r-montant" type="number" min="0" step="0.01" align="right" invalid={!!errors.montant}
          {...register("montant", { valueAsNumber: true })} />
      </FormField>
      <FormField label={titre ? "N° du titre" : "Référence"} htmlFor="r-ref" required={titre} error={errors.reference?.message}>
        <Input id="r-ref" invalid={!!errors.reference} {...register("reference")} />
      </FormField>
      <FormField label="Banque" htmlFor="r-banque">
        <Input id="r-banque" {...register("banque")} />
      </FormField>
      {titre && (
        <FormField label="Date d'échéance" htmlFor="r-echeance" hint="Vide = date du règlement">
          <Input id="r-echeance" type="date" {...register("dateEcheance")} />
        </FormField>
      )}
      <FormGridFull>
        <FormField label="Notes" htmlFor="r-notes">
          <Textarea id="r-notes" rows={2} {...register("notes")} />
        </FormField>
      </FormGridFull>
    </FormGrid>
  );
}
