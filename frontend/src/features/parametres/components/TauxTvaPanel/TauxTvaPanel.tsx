"use client";

import { useState } from "react";
import { FormGrid } from "@/components/layout/FormGrid";
import { Badge } from "@/components/ui/Badge";
import { Button } from "@/components/ui/Button";
import { Card } from "@/components/ui/Card";
import { Checkbox } from "@/components/ui/Checkbox";
import { FormField } from "@/components/ui/FormField";
import { Icon } from "@/components/ui/Icon";
import { Input } from "@/components/ui/Input";
import { Modal } from "@/components/ui/Modal";
import { Table, type Column } from "@/components/ui/Table";
import { useToast } from "@/components/ui/Toast";
import { errorMessage } from "@/lib/api-client";
import { formatPercent } from "@/lib/format";
import { useDeleteTauxTva, useSaveTauxTva, useTauxTva } from "../../hooks/useParametres";
import type { TauxTva } from "../../types/parametre.types";

interface Draft {
  id?: number;
  taux: string;
  libelle: string;
  actif: boolean;
  parDefaut: boolean;
}

export function TauxTvaPanel() {
  const toast = useToast();
  const query = useTauxTva();
  const save = useSaveTauxTva();
  const remove = useDeleteTauxTva();
  const [draft, setDraft] = useState<Draft | null>(null);

  const columns: Column<TauxTva>[] = [
    { key: "taux", header: "Taux", align: "right", render: (t) => formatPercent(t.taux) },
    { key: "libelle", header: "Libellé", render: (t) => t.libelle },
    { key: "statut", header: "Statut", render: (t) => <>{t.actif ? <Badge tone="success">Actif</Badge> : <Badge>Inactif</Badge>} {t.parDefaut && <Badge tone="primary">Par défaut</Badge>}</> },
    {
      key: "actions", header: "", align: "right",
      render: (t) => !t.parDefaut && (
        <Button size="sm" variant="ghost" aria-label={`Supprimer ${t.libelle}`} onClick={async (e) => {
          e.stopPropagation();
          try { await remove.mutateAsync(t.id); toast.success("Taux supprimé"); } catch (err) { toast.error(errorMessage(err)); }
        }}><Icon name="trash" size={15} /></Button>
      ),
    },
  ];

  const valid = draft && draft.libelle.trim() && draft.taux !== "" && Number(draft.taux) >= 0 && Number(draft.taux) <= 100;

  const submit = async () => {
    if (!draft || !valid) return;
    try {
      await save.mutateAsync({ id: draft.id, body: { taux: Number(draft.taux), libelle: draft.libelle.trim(), actif: draft.actif, parDefaut: draft.parDefaut } });
      toast.success("Taux de TVA enregistré");
      setDraft(null);
    } catch (e) {
      toast.error(errorMessage(e));
    }
  };

  return (
    <Card title="Taux de TVA" padded={false}
      actions={<Button size="sm" icon={<Icon name="plus" size={14} />} onClick={() => setDraft({ taux: "", libelle: "", actif: true, parDefaut: false })}>Ajouter un taux</Button>}>
      <Table columns={columns} rows={query.data} rowKey={(t) => t.id} loading={query.isLoading} error={query.error}
        onRowClick={(t) => setDraft({ id: t.id, taux: String(Number(t.taux)), libelle: t.libelle, actif: t.actif, parDefaut: t.parDefaut })} />
      <Modal open={!!draft} onClose={() => setDraft(null)} size="sm" title={draft?.id ? "Modifier le taux" : "Nouveau taux de TVA"}
        footer={<><Button variant="ghost" onClick={() => setDraft(null)}>Annuler</Button>
          <Button variant="primary" onClick={submit} loading={save.isPending} disabled={!valid}>Enregistrer</Button></>}>
        {draft && (
          <FormGrid>
            <FormField label="Taux (%)" htmlFor="tva-taux" required>
              <Input id="tva-taux" type="number" min="0" max="100" step="0.01" align="right" value={draft.taux}
                onChange={(e) => setDraft({ ...draft, taux: e.target.value })} />
            </FormField>
            <FormField label="Libellé" htmlFor="tva-lib" required>
              <Input id="tva-lib" value={draft.libelle} onChange={(e) => setDraft({ ...draft, libelle: e.target.value })} />
            </FormField>
            <Checkbox label="Actif" checked={draft.actif} onChange={(e) => setDraft({ ...draft, actif: e.target.checked })} />
            <Checkbox label="Taux par défaut" checked={draft.parDefaut} onChange={(e) => setDraft({ ...draft, parDefaut: e.target.checked })} />
          </FormGrid>
        )}
      </Modal>
    </Card>
  );
}
