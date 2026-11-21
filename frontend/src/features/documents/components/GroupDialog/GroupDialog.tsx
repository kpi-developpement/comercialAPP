"use client";

import { useRouter } from "next/navigation";
import { useEffect, useState } from "react";
import { FormGrid } from "@/components/layout/FormGrid";
import { Button } from "@/components/ui/Button";
import { Checkbox } from "@/components/ui/Checkbox";
import { EmptyState } from "@/components/ui/EmptyState";
import { FormField } from "@/components/ui/FormField";
import { Input } from "@/components/ui/Input";
import { Modal } from "@/components/ui/Modal";
import { Skeleton } from "@/components/ui/Skeleton";
import { useToast } from "@/components/ui/Toast";
import { TiersPicker } from "@/features/tiers/components/TiersPicker";
import { errorMessage } from "@/lib/api-client";
import { formatDate, formatMoney, todayIso } from "@/lib/format";
import { DOCUMENT_TYPES, routeOf, type DocumentTypeConfig } from "../../config/documentTypes";
import { useGroupDocuments } from "../../hooks/useDocumentMutations";
import { useDocuments } from "../../hooks/useDocuments";
import styles from "./GroupDialog.module.css";

interface GroupDialogProps {
  open: boolean;
  config: DocumentTypeConfig;
  onClose: () => void;
}

/** Groups several BL (or réceptions) of the same tiers into one draft invoice. */
export function GroupDialog({ open, config, onClose }: GroupDialogProps) {
  const router = useRouter();
  const toast = useToast();
  const source = DOCUMENT_TYPES[config.groupFrom!];
  const group = useGroupDocuments(config.domain);
  const [tiers, setTiers] = useState<{ id: number; nom: string } | null>(null);
  const [selected, setSelected] = useState<number[]>([]);
  const [date, setDate] = useState(todayIso());
  const docs = useDocuments(
    source.type,
    { page: 0, size: 100, tiersId: tiers?.id, transformable: true, sort: "dateDocument,asc" },
    open && !!tiers,
  );

  useEffect(() => {
    if (!open) {
      setTiers(null);
      setSelected([]);
    }
  }, [open]);

  const toggle = (id: number) => setSelected((s) => (s.includes(id) ? s.filter((x) => x !== id) : [...s, id]));

  const submit = async () => {
    try {
      const facture = await group.mutateAsync({ documentIds: selected, dateDocument: date });
      toast.success("Facture brouillon créée");
      onClose();
      router.push(routeOf(facture.type, facture.id));
    } catch (e) {
      toast.error(errorMessage(e));
    }
  };

  return (
    <Modal
      open={open}
      onClose={onClose}
      size="lg"
      title={`Regrouper des ${source.labelPlural.toLowerCase()} en une facture`}
      footer={
        <>
          <Button variant="ghost" onClick={onClose}>Annuler</Button>
          <Button variant="primary" onClick={submit} loading={group.isPending} disabled={selected.length === 0}>
            Créer la facture ({selected.length})
          </Button>
        </>
      }
    >
      <FormGrid>
        <FormField label={config.tiersKind === "clients" ? "Client" : "Fournisseur"} htmlFor="grp-tiers">
          <TiersPicker id="grp-tiers" kind={config.tiersKind} label={tiers?.nom ?? ""}
            onSelect={(t) => { setTiers({ id: t.id, nom: t.raisonSociale }); setSelected([]); }} />
        </FormField>
        <FormField label="Date de la facture" htmlFor="grp-date">
          <Input id="grp-date" type="date" value={date} onChange={(e) => setDate(e.target.value)} />
        </FormField>
      </FormGrid>
      <div className={styles.list}>
        {!tiers && <p className={styles.hint}>Choisissez un tiers pour afficher ses documents non facturés.</p>}
        {tiers && docs.isLoading && <Skeleton height={80} />}
        {tiers && docs.data?.content.length === 0 && <EmptyState title="Aucun document à facturer pour ce tiers" />}
        {docs.data?.content.map((d) => (
          <div key={d.id} className={styles.item}>
            <Checkbox label="" aria-label={`Sélectionner ${d.numero}`} checked={selected.includes(d.id)} onChange={() => toggle(d.id)} />
            <strong>{d.numero}</strong>
            <span>{formatDate(d.dateDocument)}</span>
            <span className={styles.amount}>{formatMoney(d.totalTtc)}</span>
          </div>
        ))}
      </div>
    </Modal>
  );
}
