"use client";

import type { UseFormReturn } from "react-hook-form";
import { FormGrid, FormGridFull } from "@/components/layout/FormGrid";
import { FormSection } from "@/components/layout/FormSection";
import { CheckboxField, DateField, NumberField, SelectField, TextAreaField, TextField, toOptions } from "@/components/ui/FormControls";
import { FORMES_JURIDIQUES, MODES_LIVRAISON } from "@/lib/form";
import { TYPE_REGLEMENT_LABELS, type TypeReglement } from "@/lib/types";
import type { FournisseurFormValues } from "../../schemas/fournisseur.schema";

const MODES = (Object.keys(TYPE_REGLEMENT_LABELS) as TypeReglement[]).map((m) => ({ value: m, label: TYPE_REGLEMENT_LABELS[m] }));

/** "Informations légales": legal identifiers, delivery and payment terms, financial charges. */
export function FournisseurFormLegal({ form }: { form: UseFormReturn<FournisseurFormValues> }) {
  return (
    <>
      <FormSection title="Renseignements fiscaux et juridiques">
        <FormGrid columns={4}>
          <SelectField form={form} name="formeJuridique" label="Forme juridique" options={toOptions(FORMES_JURIDIQUES)} placeholder="Non définie" />
          <TextField form={form} name="rc" label="Registre de commerce (RC)" required />
          <TextField form={form} name="patente" label="Taxe professionnelle (TP)" required />
          <NumberField form={form} name="capital" label="Capital (MAD)" />
          <TextField form={form} name="ice" label="ICE" hint="15 chiffres" />
          <TextField form={form} name="identifiantFiscal" label="Identifiant fiscal (IF)" />
          <TextField form={form} name="cnss" label="CNSS" />
          <NumberField form={form} name="effectif" label="Effectif" step="1" />
          <DateField form={form} name="dateCreationSociete" label="Date de création de la société" />
          <CheckboxField form={form} name="nonResident" label="Fournisseur non résident" />
          <CheckboxField form={form} name="commercant" label="Commerçant" />
          <CheckboxField form={form} name="validationSuperviseur" label="Demandes d'achat validées par superviseur" />
        </FormGrid>
      </FormSection>
      <FormSection title="Modalités et informations bancaires">
        <FormGrid columns={4}>
          <SelectField form={form} name="modeLivraison" label="Mode de livraison" options={toOptions(MODES_LIVRAISON)} placeholder="Non défini" />
          <NumberField form={form} name="delaiLivraisonMin" label="Délai min de livraison (jours)" step="1" />
          <NumberField form={form} name="delaiLivraisonMax" label="Délai max de livraison (jours)" step="1" />
          <SelectField form={form} name="modeReglementDefaut" label="Mode de règlement" options={MODES} placeholder="Non défini" nullable />
          <TextField form={form} name="conditionPaiement" label="Condition de paiement" />
          <NumberField form={form} name="plafondDecouvert" label="Plafond du découvert (MAD)" />
          <NumberField form={form} name="delaiPaiement" label="Durée de crédit maximale (jours)" step="1" />
          <NumberField form={form} name="plafondRemise" label="Plafond de remise accordée (%)" />
          <NumberField form={form} name="remiseDefaut" label="Remise par défaut (%)" />
          <NumberField form={form} name="escompte" label="Escompte (%)" />
          <FormGridFull>
            <TextAreaField form={form} name="commentairePaiement" label="Commentaire sur les conditions de paiement" rows={2} />
          </FormGridFull>
        </FormGrid>
      </FormSection>
      <FormSection title="Charges financières">
        <FormGrid columns={3}>
          <TextField form={form} name="imputationTva" label="Imputation TVA" />
          <NumberField form={form} name="codeTva" label="Code TVA (%)" />
          <TextField form={form} name="codeIntegration" label="Code d'intégration" />
        </FormGrid>
      </FormSection>
    </>
  );
}
