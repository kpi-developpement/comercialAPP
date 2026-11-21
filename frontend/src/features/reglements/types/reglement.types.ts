import type { DocumentSummary } from "@/features/documents/types/document.types";
import type { TiersKind } from "@/features/tiers/types/tiers.types";
import type { TypeReglement } from "@/lib/types";

/** Payment direction, expressed with the same path segment as the tiers ("clients" | "fournisseurs"). */
export type Sens = TiersKind;

export type StatutReglement = "EN_PORTEFEUILLE" | "REMIS_EN_BANQUE" | "ENCAISSE" | "IMPAYE";

export const STATUT_REGLEMENT_LABELS: Record<StatutReglement, string> = {
  EN_PORTEFEUILLE: "En portefeuille",
  REMIS_EN_BANQUE: "Remis en banque",
  ENCAISSE: "Encaissé",
  IMPAYE: "Impayé",
};

/** Allowed status changes (mirrors backend StatutReglement.transitionsAutorisees). */
export const TRANSITIONS: Record<StatutReglement, StatutReglement[]> = {
  EN_PORTEFEUILLE: ["REMIS_EN_BANQUE", "ENCAISSE", "IMPAYE"],
  REMIS_EN_BANQUE: ["ENCAISSE", "IMPAYE"],
  ENCAISSE: [],
  IMPAYE: [],
};

export interface Affectation {
  id: number;
  documentId: number;
  documentNumero: string | null;
  montant: number;
}

export interface Reglement {
  id: number;
  numero: string;
  sens: "CLIENT" | "FOURNISSEUR";
  tiersId: number;
  tiersNom: string;
  dateReglement: string;
  mode: TypeReglement;
  montant: number;
  montantAffecte: number;
  montantNonAffecte: number;
  reference: string | null;
  banque: string | null;
  dateEcheance: string | null;
  statut: StatutReglement;
  notes: string | null;
  affectations: Affectation[];
  createdBy: string | null;
  createdAt: string;
}

export interface AffectationRequest {
  documentId: number;
  montant: number;
}

export interface ReglementRequest {
  tiersId: number;
  dateReglement: string;
  mode: TypeReglement;
  montant: number;
  reference: string | null;
  banque: string | null;
  dateEcheance: string | null;
  notes: string | null;
  affectations: AffectationRequest[];
}

export interface SoldeTiers {
  tiersId: number;
  totalFactures: number;
  totalAvoirs: number;
  totalRegle: number;
  reglementsNonAffectes: number;
  solde: number;
  facturesNonSoldees: DocumentSummary[];
}
