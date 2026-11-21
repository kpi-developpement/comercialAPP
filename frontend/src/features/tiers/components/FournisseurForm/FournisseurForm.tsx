"use client";

import { zodResolver } from "@hookform/resolvers/zod";
import { useRouter } from "next/navigation";
import { useForm } from "react-hook-form";
import { Button } from "@/components/ui/Button";
import { Card } from "@/components/ui/Card";
import { useToast } from "@/components/ui/Toast";
import { errorMessage } from "@/lib/api-client";
import { useSaveFournisseur } from "../../hooks/useTiers";
import {
  fournisseurSchema,
  toFournisseurRequest,
  toFournisseurValues,
  type FournisseurFormValues,
} from "../../schemas/fournisseur.schema";
import type { Fournisseur } from "../../types/tiers.types";
import { FournisseurFormAttestation } from "./FournisseurFormAttestation";
import { FournisseurFormGeneral } from "./FournisseurFormGeneral";
import { FournisseurFormLegal } from "./FournisseurFormLegal";
import styles from "./FournisseurForm.module.css";

export type FournisseurFormSection = "general" | "legal" | "attestation";

interface Props {
  fournisseur?: Fournisseur;
  section: FournisseurFormSection;
  readOnly?: boolean;
}

/** "Fichier fournisseur" form; the three sections stay mounted so one save covers every tab. */
export function FournisseurForm({ fournisseur, section, readOnly = false }: Props) {
  const router = useRouter();
  const toast = useToast();
  const save = useSaveFournisseur();
  const form = useForm<FournisseurFormValues>({
    resolver: zodResolver(fournisseurSchema),
    mode: "onBlur",
    values: toFournisseurValues(fournisseur),
    resetOptions: { keepDirtyValues: true },
  });

  const onSubmit = async (v: FournisseurFormValues) => {
    try {
      const saved = await save.mutateAsync({ id: fournisseur?.id, body: toFournisseurRequest(v) });
      toast.success(fournisseur ? "Fiche fournisseur enregistrée" : `Fournisseur ${saved.raisonSociale} créé`);
      if (!fournisseur) router.replace(`/fournisseurs/${saved.id}`);
    } catch (e) {
      toast.error(errorMessage(e));
    }
  };

  const onInvalid = () => toast.error("Certains champs sont invalides : vérifiez les trois onglets de la fiche.");

  return (
    <form className={styles.form} onSubmit={form.handleSubmit(onSubmit, onInvalid)} noValidate>
      <fieldset className={styles.fieldset} disabled={readOnly}>
        <div hidden={section !== "general"}>
          <FournisseurFormGeneral form={form} readOnly={readOnly} />
        </div>
        <div hidden={section !== "legal"}>
          <Card title="Informations légales">
            <FournisseurFormLegal form={form} />
          </Card>
        </div>
        <div hidden={section !== "attestation"}>
          <Card title="Attestation de régularité fiscale">
            <FournisseurFormAttestation form={form} />
          </Card>
        </div>
      </fieldset>
      {!readOnly && (
        <div className={styles.actions}>
          <Button variant="ghost" onClick={() => router.push("/fournisseurs")}>Retour à la liste</Button>
          <Button type="submit" variant="primary" loading={save.isPending}>
            {fournisseur ? "Enregistrer la fiche" : "Créer le fournisseur"}
          </Button>
        </div>
      )}
    </form>
  );
}
