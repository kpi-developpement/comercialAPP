"use client";

import type { UseFormReturn } from "react-hook-form";
import { FormGrid, FormGridFull } from "@/components/layout/FormGrid";
import { FormSection } from "@/components/layout/FormSection";
import { Card } from "@/components/ui/Card";
import { CheckboxField, DateField, SelectField, TextAreaField, TextField, toOptions } from "@/components/ui/FormControls";
import { RadioGroup } from "@/components/ui/RadioGroup";
import { DEVISES, PAYS } from "@/lib/form";
import type { FournisseurFormValues } from "../../schemas/fournisseur.schema";
import { FournisseurContactsTable } from "./FournisseurContactsTable";
import styles from "./FournisseurForm.module.css";

/** "Information générale": identity, contact, category, interlocuteurs, observation, activity dates. */
export function FournisseurFormGeneral({ form, readOnly }: { form: UseFormReturn<FournisseurFormValues>; readOnly: boolean }) {
  return (
    <div className={styles.stack}>
      <Card title="Information fournisseur">
        <div className={styles.withAside}>
          <FormGrid columns={3}>
            <TextField form={form} name="code" label="Code fournisseur" required />
            <TextField form={form} name="raisonSociale" label="Raison sociale" required />
            <TextField form={form} name="codeGroupe" label="Code groupe" />
            <FormGridFull>
              <TextField form={form} name="adresse" label="Adresse" required />
            </FormGridFull>
            <SelectField form={form} name="pays" label="Pays" required options={toOptions(PAYS)} />
            <TextField form={form} name="ville" label="Ville" required />
            <TextField form={form} name="codePostal" label="Code postal" />
            <TextField form={form} name="nationalite" label="Nationalité" />
            <TextField form={form} name="telephone" label="Téléphone 1" type="tel" />
            <TextField form={form} name="telephone2" label="Téléphone 2" type="tel" />
            <TextField form={form} name="fax" label="Fax" type="tel" />
            <TextField form={form} name="email" label="E-mail" type="email" />
            <TextField form={form} name="siteWeb" label="Site internet" />
            <TextField form={form} name="activite" label="Activité" />
            <SelectField form={form} name="devise" label="Devise" required options={toOptions(DEVISES)} />
            <TextField form={form} name="categorie" label="Catégorie" />
          </FormGrid>
          <RadioGroup legend="Catégorie de paiement" options={[{ value: "COMPTANT", label: "Comptant" }, { value: "A_TERME", label: "À terme" }]}
            {...form.register("typeFournisseur")} />
        </div>
      </Card>
      <Card title="Interlocuteurs" padded={false}>
        <FournisseurContactsTable form={form} readOnly={readOnly} />
      </Card>
      <Card title="Observation et activité">
        <FormSection title="Relation">
          <FormGrid columns={3}>
            <FormGridFull>
              <TextAreaField form={form} name="notes" label="Observation" />
            </FormGridFull>
            <DateField form={form} name="dateDebutActivite" label="Début de notre activité avec ce fournisseur" />
            <DateField form={form} name="dateFinActivite" label="Fin de notre activité avec ce fournisseur" />
            <CheckboxField form={form} name="actif" label="Fournisseur actif (décocher pour désactiver)" />
          </FormGrid>
        </FormSection>
      </Card>
    </div>
  );
}
