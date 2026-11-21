"use client";

import { useState } from "react";
import { Card } from "@/components/ui/Card";
import { Checkbox } from "@/components/ui/Checkbox";
import { Input } from "@/components/ui/Input";
import { Table, type Column } from "@/components/ui/Table";
import { useToast } from "@/components/ui/Toast";
import { errorMessage } from "@/lib/api-client";
import { useModesReglement, useUpdateModeReglement } from "../../hooks/useParametres";
import type { ModeReglement } from "../../types/parametre.types";

/** Inline edition of payment mode labels and activation. */
export function ModesReglementPanel() {
  const toast = useToast();
  const query = useModesReglement();
  const update = useUpdateModeReglement();
  const [labels, setLabels] = useState<Record<number, string>>({});

  const persist = async (mode: ModeReglement, patch: Partial<Pick<ModeReglement, "libelle" | "actif">>) => {
    try {
      await update.mutateAsync({ id: mode.id, libelle: patch.libelle ?? mode.libelle, actif: patch.actif ?? mode.actif });
      toast.success("Mode de règlement mis à jour");
    } catch (e) {
      toast.error(errorMessage(e));
    }
  };

  const columns: Column<ModeReglement>[] = [
    { key: "code", header: "Code", width: "140px", render: (m) => <strong>{m.code}</strong> },
    {
      key: "libelle", header: "Libellé",
      render: (m) => (
        <Input aria-label={`Libellé ${m.code}`} value={labels[m.id] ?? m.libelle}
          onChange={(e) => setLabels((l) => ({ ...l, [m.id]: e.target.value }))}
          onBlur={() => labels[m.id] !== undefined && labels[m.id] !== m.libelle && labels[m.id]!.trim() && persist(m, { libelle: labels[m.id]!.trim() })} />
      ),
    },
    { key: "actif", header: "Actif", render: (m) => <Checkbox label="Disponible" checked={m.actif} onChange={(e) => persist(m, { actif: e.target.checked })} /> },
  ];

  return (
    <Card title="Modes de règlement" padded={false}>
      <Table columns={columns} rows={query.data} rowKey={(m) => m.id} loading={query.isLoading} error={query.error} />
      {update.isPending && <p className="visually-hidden" role="status">Enregistrement…</p>}
    </Card>
  );
}
