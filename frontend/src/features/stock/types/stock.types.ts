export interface Depot {
  id: number;
  code: string;
  libelle: string;
  adresse: string | null;
  actif: boolean;
  parDefaut: boolean;
}

export type DepotRequest = Omit<Depot, "id">;

export interface EtatStock {
  articleId: number;
  code: string;
  designation: string;
  familleLibelle: string | null;
  unite: string;
  stockMinimum: number;
  quantite: number;
  pump: number;
  valeur: number;
  sousMinimum: boolean;
}

export interface StockParDepot {
  depotId: number;
  depotCode: string;
  depotLibelle: string;
  quantite: number;
}

export interface Valorisation {
  depots: { depotId: number; depotCode: string; depotLibelle: string; nbArticles: number; quantite: number; valeur: number }[];
  quantiteTotale: number;
  valeurTotale: number;
}

export type TypeMouvement =
  | "ENTREE_ACHAT"
  | "SORTIE_VENTE"
  | "RETOUR_CLIENT"
  | "RETOUR_FOURNISSEUR"
  | "TRANSFERT_ENTREE"
  | "TRANSFERT_SORTIE"
  | "AJUSTEMENT_INVENTAIRE";

export const TYPE_MOUVEMENT_LABELS: Record<TypeMouvement, string> = {
  ENTREE_ACHAT: "Entrée achat",
  SORTIE_VENTE: "Sortie vente",
  RETOUR_CLIENT: "Retour client",
  RETOUR_FOURNISSEUR: "Retour fournisseur",
  TRANSFERT_ENTREE: "Transfert entrant",
  TRANSFERT_SORTIE: "Transfert sortant",
  AJUSTEMENT_INVENTAIRE: "Ajustement inventaire",
};

export interface Mouvement {
  id: number;
  dateMouvement: string;
  articleId: number;
  articleCode: string | null;
  articleDesignation: string | null;
  depotId: number;
  depotLibelle: string | null;
  type: TypeMouvement;
  quantite: number;
  coutUnitaire: number;
  stockApres: number;
  pumpApres: number;
  referenceType: string | null;
  referenceId: number | null;
  referenceNumero: string | null;
  libelle: string | null;
  createdBy: string | null;
}

export type StatutOperation = "BROUILLON" | "VALIDE" | "ANNULE";

export interface InventaireLigne {
  id: number;
  articleId: number;
  articleCode: string;
  designation: string;
  quantiteTheorique: number;
  quantiteComptee: number | null;
  ecart: number | null;
}

export interface Inventaire {
  id: number;
  numero: string | null;
  depotId: number;
  depotLibelle: string;
  dateInventaire: string;
  statut: StatutOperation;
  notes: string | null;
  lignes: InventaireLigne[];
}

export interface InventaireSummary {
  id: number;
  numero: string | null;
  depotId: number;
  depotLibelle: string;
  dateInventaire: string;
  statut: StatutOperation;
  nbLignes: number;
  createdBy: string | null;
}

export interface TransfertLigne {
  id: number;
  articleId: number;
  articleCode: string;
  designation: string;
  quantite: number;
}

export interface Transfert {
  id: number;
  numero: string | null;
  depotSourceId: number;
  depotSourceLibelle: string;
  depotDestinationId: number;
  depotDestinationLibelle: string;
  dateTransfert: string;
  statut: StatutOperation;
  notes: string | null;
  lignes: TransfertLigne[];
  createdBy: string | null;
}

export interface TransfertRequest {
  depotSourceId: number;
  depotDestinationId: number;
  dateTransfert: string;
  notes: string | null;
  lignes: { articleId: number; quantite: number }[];
}
