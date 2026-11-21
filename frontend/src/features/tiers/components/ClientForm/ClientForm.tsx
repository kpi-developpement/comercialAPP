"use client";

import { zodResolver } from "@hookform/resolvers/zod";
import { useRouter } from "next/navigation";
import { useForm } from "react-hook-form";
import { Button } from "@/components/ui/Button";
import { Card } from "@/components/ui/Card";
import { useToast } from "@/components/ui/Toast";
import { errorMessage } from "@/lib/api-client";
import { useSaveClient } from "../../hooks/useTiers";
import { clientSchema, toClientRequest, toClientValues, type ClientFormValues } from "../../schemas/client.schema";
import type { Client } from "../../types/tiers.types";
import { ClientFormAutres } from "./ClientFormAutres";
import { ClientFormFiche } from "./ClientFormFiche";
import styles from "./ClientForm.module.css";

export type ClientFormSection = "fiche" | "autres";

interface ClientFormProps {
  client?: Client;
  section: ClientFormSection;
  readOnly?: boolean;
}

/**
 * "Fichier client" form. Both sections stay mounted (only the active one is shown) so switching tabs
 * never loses typed values; one save button submits everything.
 */
export function ClientForm({ client, section, readOnly = false }: ClientFormProps) {
  const router = useRouter();
  const toast = useToast();
  const save = useSaveClient();
  const form = useForm<ClientFormValues>({
    resolver: zodResolver(clientSchema),
    mode: "onBlur",
    values: toClientValues(client),
    resetOptions: { keepDirtyValues: true },
  });

  const onSubmit = async (v: ClientFormValues) => {
    try {
      const saved = await save.mutateAsync({ id: client?.id, body: toClientRequest(v) });
      toast.success(client ? "Fiche client enregistrée" : `Client ${saved.raisonSociale} créé`);
      if (!client) router.replace(`/clients/${saved.id}`);
    } catch (e) {
      toast.error(errorMessage(e));
    }
  };

  const onInvalid = () => toast.error("Certains champs sont invalides : vérifiez les deux onglets de la fiche.");

  return (
    <form className={styles.form} onSubmit={form.handleSubmit(onSubmit, onInvalid)} noValidate>
      <fieldset className={styles.fieldset} disabled={readOnly}>
        <div hidden={section !== "fiche"}>
          <ClientFormFiche form={form} />
        </div>
        <div hidden={section !== "autres"}>
          <Card title="Autres informations">
            <ClientFormAutres form={form} />
          </Card>
        </div>
      </fieldset>
      {!readOnly && (
        <div className={styles.actions}>
          <Button variant="ghost" onClick={() => router.push("/clients")}>Retour à la liste</Button>
          <Button type="submit" variant="primary" loading={save.isPending}>
            {client ? "Enregistrer la fiche" : "Créer le client"}
          </Button>
        </div>
      )}
    </form>
  );
}
