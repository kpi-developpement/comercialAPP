"use client";

import { useFormContext } from "react-hook-form";
import { FormGrid, FormGridFull } from "@/components/layout/FormGrid";
import { Checkbox } from "@/components/ui/Checkbox";
import { FormField } from "@/components/ui/FormField";
import { Input } from "@/components/ui/Input";
import { Select } from "@/components/ui/Select";
import { Textarea } from "@/components/ui/Textarea";
import { useDepotOptions } from "@/features/stock/hooks/useDepots";
import { TiersPicker } from "@/features/tiers/components/TiersPicker";
import type { DocumentTypeConfig } from "../../config/documentTypes";
import type { DocumentFormValues } from "../../schemas/document.schema";

interface DocumentHeaderProps {
  config: DocumentTypeConfig;
  /** Tiers cannot change once lines come from another document. */
  tiersLocked: boolean;
  onTiersSelected: (tiersId: number) => void;
}

/** Editable header: tiers, date, dépôt and type-specific fields. */
export function DocumentHeader({ config, tiersLocked, onTiersSelected }: DocumentHeaderProps) {
  const { register, setValue, watch, formState: { errors } } = useFormContext<DocumentFormValues>();
  const { options: depots } = useDepotOptions();
  const tiersNom = watch("tiersNom");
  const tiersLabel = config.tiersKind === "clients" ? "Client" : "Fournisseur";

  return (
    <FormGrid columns={4}>
      <FormField label={tiersLabel} htmlFor="doc-tiers" required error={errors.tiersId?.message}>
        <TiersPicker
          id="doc-tiers"
          kind={config.tiersKind}
          label={tiersNom}
          disabled={tiersLocked}
          invalid={!!errors.tiersId}
          onSelect={(t) => {
            setValue("tiersId", t.id, { shouldValidate: true, shouldDirty: true });
            setValue("tiersNom", t.raisonSociale);
            onTiersSelected(t.id);
          }}
        />
      </FormField>
      <FormField label="Date" htmlFor="doc-date" required error={errors.dateDocument?.message}>
        <Input id="doc-date" type="date" {...register("dateDocument")} />
      </FormField>
      <FormField label="Dépôt" htmlFor="doc-depot" required error={errors.depotId?.message}>
        <Select id="doc-depot" options={depots} placeholder="Choisir un dépôt" invalid={!!errors.depotId}
          {...register("depotId", { setValueAs: (v) => (v === "" || v === null ? null : Number(v)) })} />
      </FormField>
      {config.isFacture && (
        <FormField label="Date d'échéance" htmlFor="doc-echeance" hint="Vide = selon le délai de paiement du tiers">
          <Input id="doc-echeance" type="date" {...register("dateEcheance")} />
        </FormField>
      )}
      {config.hasValidite && (
        <FormField label="Valable jusqu'au" htmlFor="doc-validite">
          <Input id="doc-validite" type="date" {...register("dateValidite")} />
        </FormField>
      )}
      {config.hasReferenceFournisseur && (
        <FormField label="Référence fournisseur" htmlFor="doc-ref" hint="N° du document du fournisseur">
          <Input id="doc-ref" {...register("referenceFournisseur")} />
        </FormField>
      )}
      {config.isAvoir && (
        <FormField label="Marchandise" htmlFor="doc-retour">
          <Checkbox id="doc-retour" label={config.domain === "ventes" ? "Retour en stock" : "Retour au fournisseur (sortie de stock)"}
            {...register("retourMarchandise")} />
        </FormField>
      )}
      <FormGridFull>
        <FormField label="Notes" htmlFor="doc-notes" hint="Imprimées en bas du document">
          <Textarea id="doc-notes" rows={2} {...register("notes")} />
        </FormField>
      </FormGridFull>
    </FormGrid>
  );
}
