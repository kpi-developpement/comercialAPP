"use client";

import { useRouter } from "next/navigation";
import { useEffect, useState } from "react";
import { FormGrid, FormGridFull } from "@/components/layout/FormGrid";
import { Button } from "@/components/ui/Button";
import { FormField } from "@/components/ui/FormField";
import { Input } from "@/components/ui/Input";
import { Modal } from "@/components/ui/Modal";
import { Select } from "@/components/ui/Select";
import { Textarea } from "@/components/ui/Textarea";
import { useToast } from "@/components/ui/Toast";
import { useFamilleOptions } from "@/features/familles/hooks/useFamilles";
import { errorMessage } from "@/lib/api-client";
import { todayIso } from "@/lib/format";
import { useDepotOptions } from "../../hooks/useDepots";
import { useCreateInventaire } from "../../hooks/useInventaires";

/** Opens a counting session: the sheet is pre-filled with every stocked article of the dépôt. */
export function InventaireNewModal({ open, onClose }: { open: boolean; onClose: () => void }) {
  const router = useRouter();
  const toast = useToast();
  const create = useCreateInventaire();
  const { options: depots, defaultId } = useDepotOptions();
  const { data: familles = [] } = useFamilleOptions();
  const [depotId, setDepotId] = useState<string>("");
  const [date, setDate] = useState(todayIso());
  const [familleId, setFamilleId] = useState("");
  const [notes, setNotes] = useState("");

  useEffect(() => {
    if (open && defaultId && !depotId) setDepotId(String(defaultId));
  }, [open, defaultId, depotId]);

  const submit = async () => {
    try {
      const inv = await create.mutateAsync({
        depotId: Number(depotId),
        dateInventaire: date,
        familleId: familleId ? Number(familleId) : null,
        notes: notes.trim() || null,
      });
      toast.success("Feuille d'inventaire créée");
      router.push(`/stock/inventaires/${inv.id}`);
    } catch (e) {
      toast.error(errorMessage(e));
    }
  };

  return (
    <Modal
      open={open}
      onClose={onClose}
      title="Nouvel inventaire"
      footer={
        <>
          <Button variant="ghost" onClick={onClose}>Annuler</Button>
          <Button variant="primary" onClick={submit} loading={create.isPending} disabled={!depotId || !date}>Créer la feuille</Button>
        </>
      }
    >
      <FormGrid>
        <FormField label="Dépôt" htmlFor="inv-depot" required>
          <Select id="inv-depot" options={depots} value={depotId} onChange={(e) => setDepotId(e.target.value)} placeholder="Choisir" />
        </FormField>
        <FormField label="Date" htmlFor="inv-date" required>
          <Input id="inv-date" type="date" value={date} onChange={(e) => setDate(e.target.value)} />
        </FormField>
        <FormField label="Famille (optionnel)" htmlFor="inv-famille" hint="Inventaire tournant limité à une famille">
          <Select id="inv-famille" options={familles} placeholder="Tous les articles" value={familleId} onChange={(e) => setFamilleId(e.target.value)} />
        </FormField>
        <FormGridFull>
          <FormField label="Notes" htmlFor="inv-notes">
            <Textarea id="inv-notes" rows={2} value={notes} onChange={(e) => setNotes(e.target.value)} />
          </FormField>
        </FormGridFull>
      </FormGrid>
    </Modal>
  );
}
