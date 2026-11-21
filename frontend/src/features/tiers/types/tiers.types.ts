import type { TypeReglement } from "@/lib/types";

/** Clients and fournisseurs share the list screen; the kind is also the API path segment. */
export type TiersKind = "clients" | "fournisseurs";

export const TIERS_LABELS: Record<TiersKind, { singular: string; plural: string; nouveau: string }> = {
  clients: { singular: "Client", plural: "Clients", nouveau: "Nouveau client" },
  fournisseurs: { singular: "Fournisseur", plural: "Fournisseurs", nouveau: "Nouveau fournisseur" },
};

export interface TiersSummary {
  id: number;
  code: string;
  raisonSociale: string;
  ice: string | null;
  ville: string | null;
  telephone: string | null;
  email: string | null;
  actif: boolean;
  /** Clients only. */
  prospect: boolean | null;
  bloque: boolean | null;
}

export type TypeClient = "COMPTANT" | "DEPOSITAIRE" | "A_TERME" | "AUTRE";
export type RisqueClient = "NORMAL" | "LITIGIEUX" | "RISQUE";
export type TypeFournisseur = "COMPTANT" | "A_TERME";

export const TYPE_CLIENT_LABELS: Record<TypeClient, string> = {
  COMPTANT: "Comptant",
  DEPOSITAIRE: "Dépositaire",
  A_TERME: "À terme",
  AUTRE: "Autre",
};

export const RISQUE_LABELS: Record<RisqueClient, string> = {
  NORMAL: "Normal",
  LITIGIEUX: "Litigieux / Douteux",
  RISQUE: "Risqué",
};

export interface Client {
  id: number;
  code: string;
  raisonSociale: string;
  prospect: boolean;
  bloque: boolean;
  arbitrage: boolean;
  groupeSocietes: boolean;
  codeGroupe: string | null;
  typeClient: TypeClient;
  risque: RisqueClient;
  ice: string | null;
  identifiantFiscal: string | null;
  rc: string | null;
  patente: string | null;
  cnss: string | null;
  cin: string | null;
  adresse: string | null;
  pays: string;
  ville: string | null;
  codePostal: string | null;
  telephone: string | null;
  telephone2: string | null;
  fax: string | null;
  email: string | null;
  siteWeb: string | null;
  devise: string;
  representant: string | null;
  depotId: number | null;
  activite: string | null;
  categorie: string | null;
  journal: string | null;
  compteComptable: string | null;
  emailRelance: string | null;
  modeLivraison: string | null;
  modeReglementDefaut: TypeReglement | null;
  delaiPaiement: number;
  remiseDefaut: number;
  escompte: number;
  plafondCredit: number;
  dateDebutProspect: string | null;
  dateDebutActivite: string | null;
  dateFinActivite: string | null;
  dateCreationSociete: string | null;
  dateFinSociete: string | null;
  notes: string | null;
  actif: boolean;
  createdAt: string;
}

export type ClientRequest = Omit<Client, "id" | "createdAt">;

export interface FournisseurContact {
  id: number | null;
  nom: string;
  prenom: string | null;
  fonction: string | null;
  telephone: string | null;
  email: string | null;
}

export interface Fournisseur {
  id: number;
  code: string;
  raisonSociale: string;
  codeGroupe: string | null;
  typeFournisseur: TypeFournisseur;
  adresse: string | null;
  pays: string;
  ville: string | null;
  codePostal: string | null;
  nationalite: string | null;
  telephone: string | null;
  telephone2: string | null;
  fax: string | null;
  email: string | null;
  siteWeb: string | null;
  activite: string | null;
  devise: string;
  categorie: string | null;
  dateDebutActivite: string | null;
  dateFinActivite: string | null;
  notes: string | null;
  actif: boolean;
  contacts: FournisseurContact[];
  formeJuridique: string | null;
  rc: string | null;
  patente: string | null;
  capital: number;
  ice: string | null;
  identifiantFiscal: string | null;
  cnss: string | null;
  effectif: number;
  nonResident: boolean;
  commercant: boolean;
  dateCreationSociete: string | null;
  validationSuperviseur: boolean;
  modeLivraison: string | null;
  delaiLivraisonMin: number;
  delaiLivraisonMax: number;
  modeReglementDefaut: TypeReglement | null;
  conditionPaiement: string | null;
  commentairePaiement: string | null;
  plafondDecouvert: number;
  delaiPaiement: number;
  plafondRemise: number;
  remiseDefaut: number;
  escompte: number;
  imputationTva: string | null;
  codeTva: number;
  codeIntegration: string | null;
  attestationNumero: string | null;
  attestationDate: string | null;
  attestationValidite: string | null;
  situationFiscaleReguliere: boolean;
  createdAt: string;
}

export type FournisseurRequest = Omit<Fournisseur, "id" | "createdAt" | "situationFiscaleReguliere">;

/** Shape returned by GET /{kind}/{id} (used by pickers that only need common fields). */
export type Tiers = Client | Fournisseur;

export interface TiersFilters extends Record<string, string | number | boolean | undefined> {
  search: string;
  ville: string;
  actif: string;
}
