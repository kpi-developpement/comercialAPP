"use client";

import { useRouter } from "next/navigation";
import { useEffect, useState } from "react";
import { FormGrid, FormGridFull } from "@/components/layout/FormGrid";
import { Button } from "@/components/ui/Button";
import { Card } from "@/components/ui/Card";
import { FormField } from "@/components/ui/FormField";
import { Icon } from "@/components/ui/Icon";
import { Input } from "@/components/ui/Input";
import { Select } from "@/components/ui/Select";
import { Textarea } from "@/components/ui/Textarea";
import { useToast } from "@/components/ui/Toast";
import { ArticlePicker } from "@/features/documents/components/ArticlePicker";
import { errorMessage } from "@/lib/api-client";
import { todayIso } from "@/lib/format";
import { useDepotOptions } from "../../hooks/useDepots";
import { useSaveTransfert, useValiderTransfert } from "../../hooks/useTransferts";
import type { Transfert } from "../../types/stock.types";
import styles from "./TransfertForm.module.css";

interface Ligne {
  key: number;
  articleId: number | null;
  articleCode: string;
  designation: string;
  quantite: string;
}

let nextKey = 1;
const emptyLine = (): Ligne => ({ key: nextKey++, articleId: null, articleCode: "", designation: "", quantite: "1" });

/** Draft transfer between two dépôts: source, destination and stocked article lines. */
export function TransfertForm({ transfert }: { transfert?: Transfert }) {
  const router = useRouter();
  const toast = useToast();
  const save = useSaveTransfert();
  const valider = useValiderTransfert();
  const { options: depots } = useDepotOptions();
  const [source, setSource] = useState(transfert ? String(transfert.depotSourceId) : "");
  const [dest, setDest] = useState(transfert ? String(transfert.depotDestinationId) : "");
  const [date, setDate] = useState(transfert?.dateTransfert ?? todayIso());
  const [notes, setNotes] = useState(transfert?.notes ?? "");
  const [lignes, setLignes] = useState<Ligne[]>(() =>
    transfert ? transfert.lignes.map((l) => ({ key: nextKey++, articleId: l.articleId, articleCode: l.articleCode, designation: l.designation, quantite: String(Number(l.quantite)) })) : [emptyLine()],
  );
  const [error, setError] = useState<string | null>(null);

  useEffect(() => setError(null), [source, dest, lignes]);

  const update = (key: number, patch: Partial<Ligne>) => setLignes((ls) => ls.map((l) => (l.key === key ? { ...l, ...patch } : l)));

  const validate = (): string | null => {
    if (!source || !dest) return "Choisissez les dépôts source et destination";
    if (source === dest) return "Les dépôts doivent être différents";
    const valid = lignes.filter((l) => l.articleId);
    if (valid.length === 0) return "Ajoutez au moins un article";
    if (valid.some((l) => !(Number(l.quantite) > 0))) return "Les quantités doivent être positives";
    return null;
  };

  const persist = async () => {
    const err = validate();
    if (err) {
      setError(err);
      return null;
    }
    return save.mutateAsync({
      id: transfert?.id,
      body: {
        depotSourceId: Number(source),
        depotDestinationId: Number(dest),
        dateTransfert: date,
        notes: notes.trim() || null,
        lignes: lignes.filter((l) => l.articleId).map((l) => ({ articleId: l.articleId!, quantite: Number(l.quantite) })),
      },
    });
  };

  const onSave = async (andValidate: boolean) => {
    try {
      const saved = await persist();
      if (!saved) return;
      if (andValidate) {
        const v = await valider.mutateAsync(saved.id);
        toast.success(`Transfert ${v.numero} validé`);
      } else {
        toast.success("Transfert enregistré");
      }
      router.replace(`/stock/transferts/${saved.id}`);
    } catch (e) {
      toast.error(errorMessage(e));
    }
  };

  return (
    <div className={styles.stack}>
      <Card title="Transfert">
        <FormGrid columns={3}>
          <FormField label="Dépôt source" htmlFor="tf-src" required>
            <Select id="tf-src" options={depots} placeholder="Choisir" value={source} onChange={(e) => setSource(e.target.value)} />
          </FormField>
          <FormField label="Dépôt destination" htmlFor="tf-dst" required>
            <Select id="tf-dst" options={depots} placeholder="Choisir" value={dest} onChange={(e) => setDest(e.target.value)} />
          </FormField>
          <FormField label="Date" htmlFor="tf-date" required>
            <Input id="tf-date" type="date" value={date} onChange={(e) => setDate(e.target.value)} />
          </FormField>
          <FormGridFull>
            <FormField label="Notes" htmlFor="tf-notes">
              <Textarea id="tf-notes" rows={2} value={notes} onChange={(e) => setNotes(e.target.value)} />
            </FormField>
          </FormGridFull>
        </FormGrid>
      </Card>
      <Card title="Articles" actions={<Button size="sm" icon={<Icon name="plus" size={14} />} onClick={() => setLignes((ls) => [...ls, emptyLine()])}>Ajouter</Button>}>
        <table className={styles.table}>
          <thead>
            <tr><th>Article</th><th>Désignation</th><th className="num">Quantité</th><th /></tr>
          </thead>
          <tbody>
            {lignes.map((l) => (
              <tr key={l.key}>
                <td className={styles.article}>
                  <ArticlePicker label={l.articleCode} stockedOnly priceField="prixAchatHt"
                    onSelect={(a) => update(l.key, { articleId: a.id, articleCode: a.code, designation: a.designation })} />
                </td>
                <td>{l.designation || "—"}</td>
                <td className={styles.qty}>
                  <Input type="number" min="0" step="0.001" align="right" aria-label="Quantité" value={l.quantite}
                    onChange={(e) => update(l.key, { quantite: e.target.value })} />
                </td>
                <td className={styles.remove}>
                  <Button size="sm" variant="ghost" aria-label="Supprimer la ligne" onClick={() => setLignes((ls) => ls.filter((x) => x.key !== l.key))}>
                    <Icon name="trash" size={15} />
                  </Button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        {error && <p className={styles.error} role="alert">{error}</p>}
      </Card>
      <div className={styles.actions}>
        <Button variant="ghost" onClick={() => router.push("/stock/transferts")}>Retour à la liste</Button>
        <Button onClick={() => onSave(false)} loading={save.isPending && !valider.isPending}>Enregistrer le brouillon</Button>
        <Button variant="primary" onClick={() => onSave(true)} loading={valider.isPending}>Enregistrer et valider</Button>
      </div>
    </div>
  );
}
