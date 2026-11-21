"use client";

import { useState } from "react";
import { Button } from "@/components/ui/Button";
import { Card } from "@/components/ui/Card";
import { Input } from "@/components/ui/Input";
import { Table, type Column } from "@/components/ui/Table";
import { useToast } from "@/components/ui/Toast";
import { errorMessage } from "@/lib/api-client";
import { useNumerotation, useUpdateNumerotation } from "../../hooks/useParametres";
import type { NumerotationConfig } from "../../types/parametre.types";

/** Prefix and counter width per document type; numbers are assigned at validation, yearly, without gaps. */
export function NumerotationPanel() {
  const toast = useToast();
  const query = useNumerotation();
  const update = useUpdateNumerotation();
  const [edits, setEdits] = useState<Record<string, { prefixe: string; longueur: string }>>({});

  const value = (c: NumerotationConfig) => edits[c.typeDocument] ?? { prefixe: c.prefixe, longueur: String(c.longueur) };
  const set = (c: NumerotationConfig, patch: Partial<{ prefixe: string; longueur: string }>) =>
    setEdits((e) => ({ ...e, [c.typeDocument]: { ...value(c), ...patch } }));

  const save = async (c: NumerotationConfig) => {
    const v = value(c);
    if (!/^[A-Z0-9]{1,10}$/.test(v.prefixe) || Number(v.longueur) < 3 || Number(v.longueur) > 10) {
      toast.error("Préfixe : majuscules/chiffres (10 max) ; longueur entre 3 et 10");
      return;
    }
    try {
      await update.mutateAsync({ type: c.typeDocument, prefixe: v.prefixe, longueur: Number(v.longueur) });
      setEdits((e) => {
        const next = { ...e };
        delete next[c.typeDocument];
        return next;
      });
      toast.success(`Numérotation ${c.libelle} mise à jour`);
    } catch (e) {
      toast.error(errorMessage(e));
    }
  };

  const columns: Column<NumerotationConfig>[] = [
    { key: "type", header: "Document", render: (c) => c.libelle },
    { key: "prefixe", header: "Préfixe", width: "140px",
      render: (c) => <Input aria-label={`Préfixe ${c.libelle}`} value={value(c).prefixe} onChange={(e) => set(c, { prefixe: e.target.value.toUpperCase() })} /> },
    { key: "longueur", header: "Chiffres", width: "110px",
      render: (c) => <Input aria-label={`Longueur ${c.libelle}`} type="number" min="3" max="10" align="right" value={value(c).longueur} onChange={(e) => set(c, { longueur: e.target.value })} /> },
    { key: "prochain", header: "Prochain numéro", render: (c) => <code>{c.prochainNumero}</code> },
    { key: "actions", header: "", align: "right",
      render: (c) => edits[c.typeDocument] && <Button size="sm" variant="primary" onClick={() => save(c)} loading={update.isPending}>Enregistrer</Button> },
  ];

  return (
    <Card title="Numérotation des documents" padded={false}>
      <Table columns={columns} rows={query.data} rowKey={(c) => c.typeDocument} loading={query.isLoading} error={query.error} />
    </Card>
  );
}
