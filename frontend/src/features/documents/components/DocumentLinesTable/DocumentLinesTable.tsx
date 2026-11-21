"use client";

import { useEffect, useRef, useState } from "react";
import { useFieldArray, useFormContext } from "react-hook-form";
import { Button } from "@/components/ui/Button";
import { Icon } from "@/components/ui/Icon";
import { useToast } from "@/components/ui/Toast";
import { articlesApi } from "@/features/articles/api/articles.api";
import { useTauxTva } from "@/features/parametres/hooks/useParametres";
import { errorMessage } from "@/lib/api-client";
import { EMPTY_LINE, type DocumentFormValues } from "../../schemas/document.schema";
import { DocumentLineRow } from "../DocumentLineRow";
import styles from "./DocumentLinesTable.module.css";

interface DocumentLinesTableProps {
  priceField: "prixVenteHt" | "prixAchatHt";
  remiseDefaut: number;
}

/**
 * Editable lines grid. Keyboard: Enter on the discount field adds a line and focuses its article picker;
 * the scan field adds (or increments) a line from a barcode.
 */
export function DocumentLinesTable({ priceField, remiseDefaut }: DocumentLinesTableProps) {
  const toast = useToast();
  const { control, getValues, setValue, formState: { errors } } = useFormContext<DocumentFormValues>();
  const { fields, append, remove } = useFieldArray({ control, name: "lignes" });
  const { data: taux = [] } = useTauxTva();
  const tvaOptions = taux.filter((t) => t.actif).map((t) => ({ value: Number(t.taux), label: `${Number(t.taux)} %` }));
  const defaultTva = Number(taux.find((t) => t.parDefaut)?.taux ?? 20);
  const pickers = useRef<(HTMLInputElement | null)[]>([]);
  const [focusIndex, setFocusIndex] = useState<number | null>(null);
  const [scan, setScan] = useState("");

  useEffect(() => {
    if (focusIndex !== null) {
      pickers.current[focusIndex]?.focus();
      setFocusIndex(null);
    }
  }, [focusIndex, fields.length]);

  const addLine = () => {
    append({ ...EMPTY_LINE, tauxTva: defaultTva, remisePct: remiseDefaut });
    setFocusIndex(fields.length);
  };

  const onScan = async () => {
    const value = scan.trim();
    if (!value) return;
    try {
      const article = await articlesApi.scan(value);
      const lignes = getValues("lignes");
      const existing = lignes.findIndex((l) => l.articleId === article.id && l.sourceLigneId == null);
      if (existing >= 0) {
        setValue(`lignes.${existing}.quantite`, Number(lignes[existing]!.quantite) + 1, { shouldDirty: true });
      } else {
        append({
          ...EMPTY_LINE,
          articleId: article.id,
          articleCode: article.code,
          designation: article.designation,
          unite: article.unite,
          prixUnitaireHt: Number(article[priceField]),
          tauxTva: Number(article.tauxTva),
          articleStocke: article.type === "STOCKE",
          remisePct: remiseDefaut,
        });
      }
      setScan("");
    } catch (e) {
      toast.error(errorMessage(e));
    }
  };

  return (
    <div className={styles.wrapper}>
      <div className={styles.toolbar}>
        <label className={styles.scan}>
          <Icon name="barcode" size={16} />
          <span className="visually-hidden">Scanner un code-barres</span>
          <input
            className={styles.scanInput}
            placeholder="Scanner un code-barres puis Entrée"
            value={scan}
            onChange={(e) => setScan(e.target.value)}
            onKeyDown={(e) => {
              if (e.key === "Enter") {
                e.preventDefault();
                void onScan();
              }
            }}
          />
        </label>
        <Button size="sm" icon={<Icon name="plus" size={14} />} onClick={addLine}>Ajouter une ligne</Button>
      </div>
      <div className={styles.scroll}>
        <table className={styles.table}>
          <thead>
            <tr>
              <th>Article</th>
              <th>Désignation</th>
              <th className={styles.right}>Qté</th>
              <th>Unité</th>
              <th className={styles.right}>PU HT</th>
              <th className={styles.right}>Remise %</th>
              <th>TVA</th>
              <th className={styles.right}>Montant HT</th>
              <th />
            </tr>
          </thead>
          <tbody>
            {fields.map((field, index) => (
              <DocumentLineRow
                key={field.id}
                index={index}
                priceField={priceField}
                tvaOptions={tvaOptions}
                remiseDefaut={remiseDefaut}
                pickerRef={(el) => { pickers.current[index] = el; }}
                onRemove={() => remove(index)}
                onEnterNext={() => (index === fields.length - 1 ? addLine() : pickers.current[index + 1]?.focus())}
              />
            ))}
          </tbody>
        </table>
      </div>
      {fields.length === 0 && (
        <p className={styles.empty}>
          Aucune ligne. Scannez un article ou cliquez sur « Ajouter une ligne ».
        </p>
      )}
      {errors.lignes?.root?.message && <p className={styles.error} role="alert">{errors.lignes.root.message}</p>}
      {errors.lignes?.message && <p className={styles.error} role="alert">{errors.lignes.message}</p>}
    </div>
  );
}
