"use client";

import { zodResolver } from "@hookform/resolvers/zod";
import { useState } from "react";
import { useForm } from "react-hook-form";
import { FormGrid } from "@/components/layout/FormGrid";
import { Button } from "@/components/ui/Button";
import { Card } from "@/components/ui/Card";
import { ErrorState } from "@/components/ui/ErrorState";
import { TextField } from "@/components/ui/FormControls";
import { Skeleton } from "@/components/ui/Skeleton";
import { Tabs } from "@/components/ui/Tabs";
import { useToast } from "@/components/ui/Toast";
import { errorMessage } from "@/lib/api-client";
import { useSociete, useUpdateSociete } from "../../hooks/useParametres";
import { societeSchema, toSocieteRequest, toSocieteValues, type SocieteFormValues } from "../../schemas/societe.schema";
import { SocieteTabAutres } from "./SocieteTabAutres";
import { SocieteTabBancaire } from "./SocieteTabBancaire";
import { SocieteTabEntete } from "./SocieteTabEntete";
import { SocieteTabGeneral } from "./SocieteTabGeneral";
import { SocieteTabJuridique } from "./SocieteTabJuridique";
import { SocieteTabPersonne } from "./SocieteTabPersonne";
import styles from "./SocieteForm.module.css";

type Tab = "general" | "entete" | "juridique" | "bancaire" | "autres" | "personne";

/** "Fichier société": code and name on top, then six tabs; one save button for all of them. */
export function SocieteForm() {
  const toast = useToast();
  const query = useSociete();
  const save = useUpdateSociete();
  const [tab, setTab] = useState<Tab>("general");
  const form = useForm<SocieteFormValues>({
    resolver: zodResolver(societeSchema),
    mode: "onBlur",
    values: query.data ? toSocieteValues(query.data) : undefined,
  });
  const personnePhysique = form.watch("typePersonne") === "PHYSIQUE";

  if (query.isLoading) return <Skeleton height={300} />;
  if (query.error) return <ErrorState error={query.error} onRetry={() => query.refetch()} />;

  const onSubmit = async (v: SocieteFormValues) => {
    try {
      await save.mutateAsync(toSocieteRequest(v));
      toast.success("Fichier société enregistré");
    } catch (e) {
      toast.error(errorMessage(e));
    }
  };

  const tabs: { key: Tab; label: string }[] = [
    { key: "general", label: "Informations générales" },
    { key: "entete", label: "Entête et logo" },
    { key: "juridique", label: "Informations juridiques" },
    { key: "bancaire", label: "Informations bancaires" },
    { key: "autres", label: "Autres" },
    ...(personnePhysique ? [{ key: "personne" as Tab, label: "Personne physique" }] : []),
  ];

  const panel = (key: Tab, title: string, content: React.ReactNode) => (
    <div hidden={tab !== key}>
      <Card title={title}>{content}</Card>
    </div>
  );

  return (
    <form className={styles.form} onSubmit={form.handleSubmit(onSubmit, () => toast.error("Certains champs sont invalides : vérifiez les onglets."))} noValidate>
      <Card>
        <FormGrid columns={3}>
          <TextField form={form} name="code" label="Code société" />
          <TextField form={form} name="raisonSociale" label="Nom de la société" required />
        </FormGrid>
      </Card>
      <Tabs items={tabs} active={tab} onChange={setTab} />
      {panel("general", "Informations générales", <SocieteTabGeneral form={form} />)}
      {panel("entete", "Entête et logo des documents", <SocieteTabEntete form={form} />)}
      {panel("juridique", "Informations juridiques", <SocieteTabJuridique form={form} />)}
      {panel("bancaire", "Informations bancaires", <SocieteTabBancaire form={form} />)}
      {panel("autres", "Autres paramètres", <SocieteTabAutres form={form} />)}
      {personnePhysique && panel("personne", "Personne physique", <SocieteTabPersonne form={form} />)}
      <div className={styles.actions}>
        <Button type="submit" variant="primary" loading={save.isPending}>Enregistrer le fichier société</Button>
      </div>
    </form>
  );
}
