"use client";

import { useEffect, useState } from "react";
import { FormGrid } from "@/components/layout/FormGrid";
import { Button } from "@/components/ui/Button";
import { Checkbox } from "@/components/ui/Checkbox";
import { FormField } from "@/components/ui/FormField";
import { Input } from "@/components/ui/Input";
import { Modal } from "@/components/ui/Modal";
import { Select } from "@/components/ui/Select";
import { formatQty, todayIso } from "@/lib/format";
import { DOCUMENT_TYPES } from "../../config/documentTypes";
import type { DocumentDetail, TransformationRequest, TypeDocument } from "../../types/document.types";
import styles from "./TransformDialog.module.css";

interface TransformDialogProps {
  open: boolean;
  doc: DocumentDetail;
  loading: boolean;
  onClose: () => void;
  onConfirm: (request: TransformationRequest) => void;
}

/** Choose the target document and the quantity to take from each line (partial transformation). */
export function TransformDialog({ open, doc, loading, onClose, onConfirm }: TransformDialogProps) {
  const cibles = doc.transformationsPossibles;
  const [cible, setCible] = useState<TypeDocument | "">(cibles[0] ?? "");
  const [date, setDate] = useState(todayIso());
  const [retour, setRetour] = useState(true);
  const [quantites, setQuantites] = useState<Record<number, string>>({});

  useEffect(() => {
    if (open) {
      setCible(cibles[0] ?? "");
      setDate(todayIso());
      setQuantites(Object.fromEntries(doc.lignes.map((l) => [l.id, String(Number(l.quantiteRestante))])));
    }
  }, [open, doc, cibles]);

  const lignes = doc.lignes.filter((l) => Number(l.quantiteRestante) > 0);
  const invalid = lignes.some((l) => {
    const q = Number(quantites[l.id] ?? 0);
    return Number.isNaN(q) || q < 0 || q > Number(l.quantiteRestante);
  });
  const total = lignes.reduce((s, l) => s + (Number(quantites[l.id]) || 0), 0);
  const isAvoir = cible !== "" && DOCUMENT_TYPES[cible].isAvoir;

  const submit = () => {
    if (!cible) return;
    onConfirm({
      cible,
      dateDocument: date,
      retourMarchandise: isAvoir && retour,
      lignes: lignes.map((l) => ({ ligneId: l.id, quantite: Number(quantites[l.id]) || 0 })),
    });
  };

  return (
    <Modal
      open={open}
      onClose={onClose}
      title={`Transformer ${doc.numero ?? ""}`}
      size="lg"
      footer={
        <>
          <Button variant="ghost" onClick={onClose}>Annuler</Button>
          <Button variant="primary" onClick={submit} loading={loading} disabled={!cible || invalid || total <= 0}>
            Créer {cible ? DOCUMENT_TYPES[cible].label.toLowerCase() : "le document"}
          </Button>
        </>
      }
    >
      <FormGrid columns={3}>
        <FormField label="Document à créer" htmlFor="tr-cible">
          <Select id="tr-cible" value={cible} onChange={(e) => setCible(e.target.value as TypeDocument)}
            options={cibles.map((c) => ({ value: c, label: DOCUMENT_TYPES[c].label }))} />
        </FormField>
        <FormField label="Date" htmlFor="tr-date">
          <Input id="tr-date" type="date" value={date} onChange={(e) => setDate(e.target.value)} />
        </FormField>
        {isAvoir && (
          <FormField label="Marchandise" htmlFor="tr-retour">
            <Checkbox id="tr-retour" label="Retour de marchandise" checked={retour} onChange={(e) => setRetour(e.target.checked)} />
          </FormField>
        )}
      </FormGrid>
      <table className={styles.table}>
        <thead>
          <tr>
            <th>Article</th>
            <th className="num">Restant</th>
            <th className="num">Quantité à reprendre</th>
          </tr>
        </thead>
        <tbody>
          {lignes.map((l) => {
            const q = Number(quantites[l.id] ?? 0);
            const bad = Number.isNaN(q) || q < 0 || q > Number(l.quantiteRestante);
            return (
              <tr key={l.id}>
                <td>{l.articleCode ? <strong>{l.articleCode}</strong> : null} {l.designation}</td>
                <td className="num">{formatQty(l.quantiteRestante)}</td>
                <td className={styles.qty}>
                  <Input type="number" min="0" step="0.001" max={Number(l.quantiteRestante)} align="right" invalid={bad}
                    aria-label={`Quantité pour ${l.designation}`} value={quantites[l.id] ?? ""}
                    onChange={(e) => setQuantites((s) => ({ ...s, [l.id]: e.target.value }))} />
                </td>
              </tr>
            );
          })}
        </tbody>
      </table>
      <p className={styles.hint}>Mettez 0 pour ne pas reprendre une ligne. Le reliquat restera disponible pour une prochaine transformation.</p>
    </Modal>
  );
}
