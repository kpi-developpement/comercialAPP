"use client";

import type { UseFormReturn } from "react-hook-form";
import { FormGrid } from "@/components/layout/FormGrid";
import { FormSection } from "@/components/layout/FormSection";
import { Card } from "@/components/ui/Card";
import { CheckboxField, NumberField, SelectField, TextField, toOptions } from "@/components/ui/FormControls";
import { RadioGroup } from "@/components/ui/RadioGroup";
import { useDepotOptions } from "@/features/stock/hooks/useDepots";
import { DEVISES, MODES_LIVRAISON, PAYS } from "@/lib/form";
import { TYPE_REGLEMENT_LABELS, type TypeReglement } from "@/lib/types";
import type { ClientFormValues } from "../../schemas/client.schema";
import { RISQUE_LABELS, TYPE_CLIENT_LABELS, type RisqueClient, type TypeClient } from "../../types/tiers.types";
import styles from "./ClientForm.module.css";

const MODES = (Object.keys(TYPE_REGLEMENT_LABELS) as TypeReglement[]).map((m) => ({ value: m, label: TYPE_REGLEMENT_LABELS[m] }));
const TYPES = (Object.keys(TYPE_CLIENT_LABELS) as TypeClient[]).map((t) => ({ value: t, label: TYPE_CLIENT_LABELS[t] }));
const RISQUES = (Object.keys(RISQUE_LABELS) as RisqueClient[]).map((r) => ({ value: r, label: RISQUE_LABELS[r] }));

/** "Fiche client" tab: status, identity and contact, legal identifiers, commercial terms. */
export function ClientFormFiche({ form }: { form: UseFormReturn<ClientFormValues> }) {
  const { options: depots } = useDepotOptions();
  const groupe = form.watch("groupeSocietes");
  return (
    <div className={styles.stack}>
      <Card title="Statut">
        <div className={styles.statusRow}>
          <RadioGroup legend="Nature" direction="row" options={[{ value: "CLIENT", label: "Client" }, { value: "PROSPECT", label: "Prospect" }]}
            {...form.register("nature")} />
          <RadioGroup legend="Type" options={TYPES} {...form.register("typeClient")} />
          <RadioGroup legend="Risque client" options={RISQUES} {...form.register("risque")} />
          <div className={styles.flags}>
            <CheckboxField form={form} name="bloque" label="Compte client bloqué" />
            <CheckboxField form={form} name="arbitrage" label="Arbitrage" />
            <CheckboxField form={form} name="groupeSocietes" label="Groupe de sociétés" />
            <TextField form={form} name="codeGroupe" label="Code groupe" disabled={!groupe} />
          </div>
        </div>
      </Card>
      <Card title="Identité et coordonnées">
        <FormSection title="Client">
          <FormGrid columns={4}>
            <TextField form={form} name="code" label="Code client" required />
            <TextField form={form} name="raisonSociale" label="Raison sociale" required />
            <TextField form={form} name="adresse" label="Adresse" />
            <SelectField form={form} name="pays" label="Pays" required options={toOptions(PAYS)} />
            <TextField form={form} name="ville" label="Ville" />
            <TextField form={form} name="codePostal" label="Code postal" />
            <SelectField form={form} name="devise" label="Devise" required options={toOptions(DEVISES)} />
            <TextField form={form} name="telephone" label="Téléphone 1" type="tel" />
            <TextField form={form} name="telephone2" label="Téléphone 2" type="tel" />
            <TextField form={form} name="fax" label="Fax" type="tel" />
            <TextField form={form} name="email" label="E-mail" type="email" />
            <TextField form={form} name="siteWeb" label="Site internet" />
            <TextField form={form} name="representant" label="Représentant" />
            <SelectField form={form} name="depotId" label="Dépôt" options={depots} placeholder="Dépôt par défaut" numeric />
          </FormGrid>
        </FormSection>
        <FormSection title="Identifiants légaux">
          <FormGrid columns={3}>
            <TextField form={form} name="ice" label="ICE" hint="15 chiffres" />
            <TextField form={form} name="identifiantFiscal" label="Identifiant fiscal (I.F)" />
            <TextField form={form} name="rc" label="Registre de commerce (R.C)" />
            <TextField form={form} name="patente" label="Taxe professionnelle (T.P)" />
            <TextField form={form} name="cnss" label="CNSS" />
            <TextField form={form} name="cin" label="CIN" hint="Personne physique" />
          </FormGrid>
        </FormSection>
        <FormSection title="Conditions commerciales">
          <FormGrid columns={4}>
            <SelectField form={form} name="modeLivraison" label="Mode de livraison" options={toOptions(MODES_LIVRAISON)} placeholder="Non défini" />
            <SelectField form={form} name="modeReglementDefaut" label="Mode de règlement" options={MODES} placeholder="Non défini" nullable />
            <NumberField form={form} name="plafondCredit" label="Découvert autorisé (MAD)" hint="0 = sans limite" />
            <NumberField form={form} name="delaiPaiement" label="Durée crédit (jours)" step="1" />
            <NumberField form={form} name="escompte" label="Escompte (%)" />
            <NumberField form={form} name="remiseDefaut" label="Remise par défaut (%)" />
            <TextField form={form} name="categorie" label="Catégorie" />
          </FormGrid>
        </FormSection>
      </Card>
    </div>
  );
}
