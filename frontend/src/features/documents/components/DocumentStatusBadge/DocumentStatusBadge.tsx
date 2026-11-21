import { Badge, type BadgeTone } from "@/components/ui/Badge";
import type { StatutDocument, StatutPaiement } from "../../types/document.types";

const STATUT: Record<StatutDocument, { label: string; tone: BadgeTone }> = {
  BROUILLON: { label: "Brouillon", tone: "neutral" },
  VALIDE: { label: "Validé", tone: "primary" },
  TRANSFORME_PARTIEL: { label: "Transformé partiellement", tone: "warning" },
  TRANSFORME: { label: "Transformé", tone: "success" },
  ANNULE: { label: "Annulé", tone: "danger" },
};

const PAIEMENT: Record<StatutPaiement, { label: string; tone: BadgeTone }> = {
  NON_PAYEE: { label: "Non payée", tone: "danger" },
  PARTIELLE: { label: "Partiellement payée", tone: "warning" },
  PAYEE: { label: "Payée", tone: "success" },
};

export const STATUT_OPTIONS = (Object.keys(STATUT) as StatutDocument[]).map((s) => ({ value: s, label: STATUT[s].label }));
export const PAIEMENT_OPTIONS = (Object.keys(PAIEMENT) as StatutPaiement[]).map((s) => ({ value: s, label: PAIEMENT[s].label }));

export function DocumentStatusBadge({ statut }: { statut: StatutDocument }) {
  const s = STATUT[statut];
  return <Badge tone={s.tone}>{s.label}</Badge>;
}

export function PaymentStatusBadge({ statut }: { statut: StatutPaiement | null }) {
  if (!statut) return null;
  const s = PAIEMENT[statut];
  return <Badge tone={s.tone}>{s.label}</Badge>;
}
