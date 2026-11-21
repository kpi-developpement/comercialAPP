import { Badge, type BadgeTone } from "@/components/ui/Badge";
import { STATUT_REGLEMENT_LABELS, type StatutReglement } from "../../types/reglement.types";

const TONES: Record<StatutReglement, BadgeTone> = {
  EN_PORTEFEUILLE: "info",
  REMIS_EN_BANQUE: "warning",
  ENCAISSE: "success",
  IMPAYE: "danger",
};

export function ReglementStatusBadge({ statut }: { statut: StatutReglement }) {
  return <Badge tone={TONES[statut]}>{STATUT_REGLEMENT_LABELS[statut]}</Badge>;
}
