"use client";

import type { UseFormReturn } from "react-hook-form";
import { FormGrid, FormGridFull } from "@/components/layout/FormGrid";
import { DateField, NumberField, TextAreaField, TextField } from "@/components/ui/FormControls";
import { RadioGroup } from "@/components/ui/RadioGroup";
import type { SocieteFormValues } from "../../schemas/societe.schema";
import styles from "./SocieteForm.module.css";

export function SocieteTabGeneral({ form }: { form: UseFormReturn<SocieteFormValues> }) {
  return (
    <FormGrid columns={3}>
      <TextField form={form} name="ice" label="I.C.E" hint="15 chiffres" />
      <TextField form={form} name="ville" label="Ville" />
      <TextField form={form} name="codePostal" label="Code postal" />
      <FormGridFull>
        <TextAreaField form={form} name="adresse" label="Adresse" rows={2} />
      </FormGridFull>
      <TextField form={form} name="telephone" label="Téléphone 1" type="tel" />
      <TextField form={form} name="telephone2" label="Téléphone 2" type="tel" />
      <TextField form={form} name="fax" label="Fax" type="tel" />
      <TextField form={form} name="codeCommune" label="Code commune" />
      <TextField form={form} name="email" label="E-mail" type="email" />
      <TextField form={form} name="siteWeb" label="Site web" />
      <FormGridFull>
        <TextAreaField form={form} name="observation" label="Observation" rows={2} />
      </FormGridFull>
      <DateField form={form} name="dateCreation" label="Date de création" />
      <DateField form={form} name="dateDebutActivite" label="Date début d'activité" />
      <DateField form={form} name="dateFinActivite" label="Date fin d'activité" />
      <FormGridFull>
        <div className={styles.radios}>
          <RadioGroup legend="Personne" options={[{ value: "MORALE", label: "Personne morale" }, { value: "PHYSIQUE", label: "Personne physique" }]}
            {...form.register("typePersonne")} />
          <RadioGroup legend="Secteur" options={[{ value: "PRIVE", label: "Privé" }, { value: "PUBLIC", label: "Public" }]}
            {...form.register("secteur")} />
          <NumberField form={form} name="loyerAnnuel" label="Loyer annuel (MAD)" />
        </div>
      </FormGridFull>
    </FormGrid>
  );
}
