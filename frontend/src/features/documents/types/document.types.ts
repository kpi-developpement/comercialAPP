export type TypeDocument =
  | "DEVIS"
  | "BON_COMMANDE"
  | "BON_LIVRAISON"
  | "FACTURE"
  | "AVOIR"
  | "BC_FOURNISSEUR"
  | "RECEPTION"
  | "FACTURE_FOURNISSEUR"
  | "AVOIR_FOURNISSEUR";

export type StatutDocument = "BROUILLON" | "VALIDE" | "TRANSFORME_PARTIEL" | "TRANSFORME" | "ANNULE";

export type StatutPaiement = "NON_PAYEE" | "PARTIELLE" | "PAYEE";

export interface DocumentLigne {
  id: number;
  ordre: number;
  articleId: number | null;
  articleCode: string | null;
  designation: string;
  unite: string | null;
  quantite: number;
  prixUnitaireHt: number;
  remisePct: number;
  tauxTva: number;
  montantRemise: number;
  montantHt: number;
  montantTva: number;
  montantTtc: number;
  quantiteRestante: number;
  sourceLigneId: number | null;
  sourceDocumentId: number | null;
  articleStocke: boolean;
}

export interface TvaRecap {
  taux: number;
  baseHt: number;
  montantTva: number;
}

export interface DocumentDetail {
  id: number;
  type: TypeDocument;
  numero: string | null;
  dateDocument: string;
  statut: StatutDocument;
  tiersId: number;
  tiersCode: string;
  tiersNom: string;
  tiersIce: string | null;
  tiersAdresse: string | null;
  depotId: number;
  depotLibelle: string;
  notes: string | null;
  totalHt: number;
  totalRemise: number;
  totalTva: number;
  totalTtc: number;
  dateEcheance: string | null;
  dateValidite: string | null;
  montantRegle: number;
  resteAPayer: number;
  statutPaiement: StatutPaiement | null;
  retourMarchandise: boolean;
  sources: string | null;
  referenceFournisseur: string | null;
  montantEnLettres: string;
  lignes: DocumentLigne[];
  recapTva: TvaRecap[];
  transformationsPossibles: TypeDocument[];
  warnings: string[];
  createdBy: string | null;
  createdAt: string;
  updatedAt: string;
  version: number;
}

export interface DocumentSummary {
  id: number;
  type: TypeDocument;
  numero: string | null;
  dateDocument: string;
  statut: StatutDocument;
  tiersId: number;
  tiersCode: string;
  tiersNom: string;
  totalHt: number;
  totalTtc: number;
  dateEcheance: string | null;
  montantRegle: number;
  resteAPayer: number;
  statutPaiement: StatutPaiement | null;
  sources: string | null;
  createdBy: string | null;
}

export interface DocumentLigneRequest {
  articleId: number | null;
  designation: string | null;
  quantite: number;
  prixUnitaireHt: number | null;
  remisePct: number | null;
  tauxTva: number | null;
  sourceLigneId: number | null;
}

export interface DocumentRequest {
  tiersId: number;
  dateDocument: string;
  depotId: number;
  notes: string | null;
  dateEcheance: string | null;
  dateValidite: string | null;
  retourMarchandise: boolean;
  referenceFournisseur: string | null;
  lignes: DocumentLigneRequest[];
}

export interface TransformationRequest {
  cible: TypeDocument;
  dateDocument: string | null;
  retourMarchandise: boolean;
  lignes: { ligneId: number; quantite: number }[];
}

export interface DocumentFilters extends Record<string, string | number | boolean | undefined> {
  search: string;
  statut: string;
  statutPaiement: string;
  dateDebut: string;
  dateFin: string;
}
