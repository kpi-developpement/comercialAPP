"use client";

import { zodResolver } from "@hookform/resolvers/zod";
import { useRouter } from "next/navigation";
import { useState } from "react";
import { FormProvider, useForm, useWatch } from "react-hook-form";
import { Button } from "@/components/ui/Button";
import { Card } from "@/components/ui/Card";
import { useToast } from "@/components/ui/Toast";
import { useDepotOptions } from "@/features/stock/hooks/useDepots";
import { useTiers } from "@/features/tiers/hooks/useTiers";
import { errorMessage } from "@/lib/api-client";
import { DOCUMENT_TYPES, routeOf } from "../../config/documentTypes";
import { useSaveDocument, useValiderDocument } from "../../hooks/useDocumentMutations";
import { documentSchema, type DocumentFormValues } from "../../schemas/document.schema";
import type { DocumentDetail, TypeDocument } from "../../types/document.types";
import { toFormValues, toRequest } from "../../utils/formMapping";
import { computeTotals } from "../../utils/totals";
import { DocumentHeader } from "../DocumentHeader";
import { DocumentLinesTable } from "../DocumentLinesTable";
import { DocumentTotals } from "../DocumentTotals";
import styles from "./DocumentEditor.module.css";

interface DocumentEditorProps {
  type: TypeDocument;
  document?: DocumentDetail;
}

/** Create / edit a draft. Totals are shown live; the backend recomputes them on save. */
export function DocumentEditor({ type, document }: DocumentEditorProps) {
  const config = DOCUMENT_TYPES[type];
  const router = useRouter();
  const toast = useToast();
  const save = useSaveDocument(type);
  const valider = useValiderDocument(type);
  const { defaultId } = useDepotOptions();
  const [tiersId, setTiersId] = useState<number | null>(document?.tiersId ?? null);
  const tiers = useTiers(config.tiersKind, tiersId);
  const form = useForm<DocumentFormValues>({
    resolver: zodResolver(documentSchema),
    mode: "onBlur",
    values: toFormValues(document, defaultId),
    resetOptions: { keepDirtyValues: true },
  });
  const lignes = useWatch({ control: form.control, name: "lignes" }) ?? [];
  const totals = computeTotals(lignes.map((l) => ({
    quantite: Number(l.quantite), prixUnitaireHt: Number(l.prixUnitaireHt), remisePct: Number(l.remisePct), tauxTva: Number(l.tauxTva),
  })));
  const tiersLocked = lignes.some((l) => l.sourceLigneId != null);

  const persist = async (values: DocumentFormValues) => {
    const saved = await save.mutateAsync({ id: document?.id, body: toRequest(values) });
    form.reset(toFormValues(saved, defaultId));
    return saved;
  };

  const onSave = async (values: DocumentFormValues) => {
    try {
      const saved = await persist(values);
      toast.success("Brouillon enregistré");
      if (!document) router.replace(routeOf(type, saved.id));
    } catch (e) {
      toast.error(errorMessage(e));
    }
  };

  const onSaveAndValidate = async (values: DocumentFormValues) => {
    try {
      const saved = await persist(values);
      const validated = await valider.mutateAsync(saved.id);
      toast.success(`${config.label} ${validated.numero} validé`);
      validated.warnings.forEach((w) => toast.info(w));
      router.replace(routeOf(type, validated.id));
    } catch (e) {
      toast.error(errorMessage(e));
    }
  };

  const busy = save.isPending || valider.isPending;
  return (
    <FormProvider {...form}>
      <form className={styles.form} onSubmit={form.handleSubmit(onSave)} noValidate>
        <Card title="En-tête">
          <DocumentHeader config={config} tiersLocked={tiersLocked} onTiersSelected={setTiersId} />
        </Card>
        <Card title="Lignes" padded={false}>
          <DocumentLinesTable
            priceField={config.domain === "ventes" ? "prixVenteHt" : "prixAchatHt"}
            remiseDefaut={Number(tiers.data?.remiseDefaut ?? 0)}
          />
          <DocumentTotals {...totals} />
        </Card>
        <div className={styles.actions}>
          <Button variant="ghost" onClick={() => router.push(routeOf(type))} disabled={busy}>Retour à la liste</Button>
          <Button type="submit" loading={save.isPending && !valider.isPending} disabled={busy}>Enregistrer le brouillon</Button>
          <Button variant="primary" onClick={form.handleSubmit(onSaveAndValidate)} loading={valider.isPending} disabled={busy}>
            Enregistrer et valider
          </Button>
        </div>
      </form>
    </FormProvider>
  );
}
