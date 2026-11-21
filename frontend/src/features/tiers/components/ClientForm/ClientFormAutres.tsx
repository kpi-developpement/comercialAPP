"use client";

import type { UseFormReturn } from "react-hook-form";
import { FormGrid, FormGridFull } from "@/components/layout/FormGrid";
import { FormSection } from "@/components/layout/FormSection";
import { CheckboxField, DateField, TextAreaField, TextField } from "@/components/ui/FormControls";
import type { ClientFormValues } from "../../schemas/client.schema";

/** "Autres informations" tab: activity, accounting, reminders and key dates. */
export function ClientFormAutres({ form }: { form: UseFormReturn<ClientFormValues> }) {
  return (
    <>
      <FormSection title="Activité et comptabilité">
        <FormGrid columns={3}>
          <TextField form={form} name="activite" label="Activité" />
          <TextField form={form} name="journal" label="Journal" />
          <TextField form={form} name="compteComptable" label="Compte N°" />
          <TextField form={form} name="emailRelance" label="E-mail de relance" type="email" hint="Destinataire des relances d'impayés" />
          <FormGridFull>
            <TextAreaField form={form} name="notes" label="Observation" />
          </FormGridFull>
        </FormGrid>
      </FormSection>
      <FormSection title="Dates">
        <FormGrid columns={3}>
          <DateField form={form} name="dateDebutProspect" label="Début du prospect" />
          <DateField form={form} name="dateDebutActivite" label="Début de notre activité avec ce client" />
          <DateField form={form} name="dateFinActivite" label="Fin de notre activité avec ce client" />
          <DateField form={form} name="dateCreationSociete" label="Date de création de la société" />
          <DateField form={form} name="dateFinSociete" label="Date de fin de la société" />
        </FormGrid>
      </FormSection>
      <FormSection title="Statut du compte">
        <CheckboxField form={form} name="actif" label="Client actif" />
      </FormSection>
    </>
  );
}
