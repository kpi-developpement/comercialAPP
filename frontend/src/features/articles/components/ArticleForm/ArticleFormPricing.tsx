"use client";

import type { UseFormReturn } from "react-hook-form";
import { FormGrid } from "@/components/layout/FormGrid";
import { FormField } from "@/components/ui/FormField";
import { Input } from "@/components/ui/Input";
import { Select } from "@/components/ui/Select";
import type { TauxTva } from "@/features/parametres/types/parametre.types";
import { formatMoney, formatPercent } from "@/lib/format";
import type { ArticleFormValues } from "../../schemas/article.schema";
import styles from "./ArticleForm.module.css";

interface Props {
  form: UseFormReturn<ArticleFormValues>;
  taux: TauxTva[];
}

export function ArticleFormPricing({ form, taux }: Props) {
  const { register, watch, formState: { errors } } = form;
  const [achat, vente, tva] = watch(["prixAchatHt", "prixVenteHt", "tauxTva"]);
  const ttc = (Number(vente) || 0) * (1 + (Number(tva) || 0) / 100);
  const marge = (Number(vente) || 0) - (Number(achat) || 0);
  const tauxMarge = Number(vente) > 0 ? (marge / Number(vente)) * 100 : 0;
  const options = taux.filter((t) => t.actif).map((t) => ({ value: Number(t.taux), label: t.libelle }));

  return (
    <>
      <FormGrid columns={4}>
        <FormField label="Prix d'achat HT" htmlFor="a-achat" error={errors.prixAchatHt?.message}>
          <Input id="a-achat" type="number" step="0.001" min="0" align="right" invalid={!!errors.prixAchatHt}
            {...register("prixAchatHt", { valueAsNumber: true })} />
        </FormField>
        <FormField label="Prix de vente HT" htmlFor="a-vente" error={errors.prixVenteHt?.message}>
          <Input id="a-vente" type="number" step="0.001" min="0" align="right" invalid={!!errors.prixVenteHt}
            {...register("prixVenteHt", { valueAsNumber: true })} />
        </FormField>
        <FormField label="Taux de TVA" htmlFor="a-tva" error={errors.tauxTva?.message}>
          <Select id="a-tva" options={options} {...register("tauxTva", { valueAsNumber: true })} />
        </FormField>
        <FormField label="Unité" htmlFor="a-unite" error={errors.unite?.message} hint="U, KG, M, H, BT…">
          <Input id="a-unite" invalid={!!errors.unite} {...register("unite")} />
        </FormField>
      </FormGrid>
      <dl className={styles.summary}>
        <div><dt>Prix de vente TTC</dt><dd>{formatMoney(ttc)}</dd></div>
        <div><dt>Marge unitaire</dt><dd>{formatMoney(marge)}</dd></div>
        <div><dt>Taux de marge</dt><dd>{formatPercent(tauxMarge.toFixed(2))}</dd></div>
      </dl>
    </>
  );
}
