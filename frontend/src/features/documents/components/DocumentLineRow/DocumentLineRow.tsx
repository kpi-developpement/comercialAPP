"use client";

import { useFormContext, useWatch } from "react-hook-form";
import type { Ref } from "react";
import { Button } from "@/components/ui/Button";
import { Icon } from "@/components/ui/Icon";
import { Input } from "@/components/ui/Input";
import { Select } from "@/components/ui/Select";
import type { Article, ArticleSummary } from "@/features/articles/types/article.types";
import type { Option } from "@/lib/types";
import { formatMoney } from "@/lib/format";
import type { DocumentFormValues } from "../../schemas/document.schema";
import { computeLine } from "../../utils/totals";
import { ArticlePicker } from "../ArticlePicker";
import styles from "./DocumentLineRow.module.css";

interface DocumentLineRowProps {
  index: number;
  priceField: "prixVenteHt" | "prixAchatHt";
  tvaOptions: Option<number>[];
  remiseDefaut: number;
  pickerRef: Ref<HTMLInputElement>;
  onRemove: () => void;
  /** Enter on the last input of the row: add a new line. */
  onEnterNext: () => void;
}

export function DocumentLineRow({ index, priceField, tvaOptions, remiseDefaut, pickerRef, onRemove, onEnterNext }: DocumentLineRowProps) {
  const { register, setValue, formState: { errors } } = useFormContext<DocumentFormValues>();
  const line = useWatch<DocumentFormValues, `lignes.${number}`>({ name: `lignes.${index}` });
  const lineErrors = errors.lignes?.[index];
  const linked = line?.sourceLigneId != null;
  const amounts = computeLine({
    quantite: Number(line?.quantite),
    prixUnitaireHt: Number(line?.prixUnitaireHt),
    remisePct: Number(line?.remisePct),
    tauxTva: Number(line?.tauxTva),
  });

  const onArticle = (a: ArticleSummary | Article) => {
    const opts = { shouldDirty: true, shouldValidate: true };
    setValue(`lignes.${index}.articleId`, a.id, opts);
    setValue(`lignes.${index}.articleCode`, a.code, opts);
    setValue(`lignes.${index}.designation`, a.designation, opts);
    setValue(`lignes.${index}.unite`, a.unite, opts);
    setValue(`lignes.${index}.prixUnitaireHt`, Number(a[priceField]), opts);
    setValue(`lignes.${index}.tauxTva`, Number(a.tauxTva), opts);
    setValue(`lignes.${index}.articleStocke`, a.type === "STOCKE", opts);
    if (!line?.articleId) setValue(`lignes.${index}.remisePct`, remiseDefaut, opts);
  };

  const num = { valueAsNumber: true } as const;
  return (
    <tr className={styles.row}>
      <td className={styles.article}>
        {linked ? (
          <span className={styles.linked} title="Ligne issue d'un document source">{line?.articleCode || "—"}</span>
        ) : (
          <ArticlePicker label={line?.articleCode ?? ""} onSelect={onArticle} priceField={priceField} inputRef={pickerRef}
            ariaLabel={`Article ligne ${index + 1}`} />
        )}
      </td>
      <td>
        <Input aria-label={`Désignation ligne ${index + 1}`} invalid={!!lineErrors?.designation} title={lineErrors?.designation?.message}
          {...register(`lignes.${index}.designation`)} />
      </td>
      <td className={styles.small}>
        <Input aria-label={`Quantité ligne ${index + 1}`} type="number" step="0.001" min="0" align="right"
          invalid={!!lineErrors?.quantite} title={lineErrors?.quantite?.message} {...register(`lignes.${index}.quantite`, num)} />
      </td>
      <td className={styles.unite}>{line?.unite}</td>
      <td className={styles.medium}>
        <Input aria-label={`Prix unitaire HT ligne ${index + 1}`} type="number" step="0.001" min="0" align="right"
          invalid={!!lineErrors?.prixUnitaireHt} {...register(`lignes.${index}.prixUnitaireHt`, num)} />
      </td>
      <td className={styles.small}>
        <Input aria-label={`Remise ligne ${index + 1}`} type="number" step="0.01" min="0" max="100" align="right"
          invalid={!!lineErrors?.remisePct}
          {...register(`lignes.${index}.remisePct`, num)}
          onKeyDown={(e) => {
            if (e.key === "Enter") {
              e.preventDefault();
              onEnterNext();
            }
          }} />
      </td>
      <td className={styles.small}>
        <Select aria-label={`TVA ligne ${index + 1}`} options={tvaOptions} {...register(`lignes.${index}.tauxTva`, num)} />
      </td>
      <td className="num">{formatMoney(amounts.ht)}</td>
      <td className={styles.remove}>
        <Button size="sm" variant="ghost" onClick={onRemove} aria-label={`Supprimer la ligne ${index + 1}`}>
          <Icon name="trash" size={15} />
        </Button>
      </td>
    </tr>
  );
}
