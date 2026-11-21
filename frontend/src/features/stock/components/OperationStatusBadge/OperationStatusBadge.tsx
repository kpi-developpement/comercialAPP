import { Badge } from "@/components/ui/Badge";
import type { StatutOperation } from "../../types/stock.types";

const LABELS: Record<StatutOperation, string> = { BROUILLON: "Brouillon", VALIDE: "Validé", ANNULE: "Annulé" };

export function OperationStatusBadge({ statut }: { statut: StatutOperation }) {
  const tone = statut === "VALIDE" ? "success" : statut === "ANNULE" ? "danger" : "neutral";
  return <Badge tone={tone}>{LABELS[statut]}</Badge>;
}
