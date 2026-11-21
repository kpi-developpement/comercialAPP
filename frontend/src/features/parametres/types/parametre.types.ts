import type { TypeReglement } from "@/lib/types";

export type TypePersonne = "MORALE" | "PHYSIQUE";
export type SecteurSociete = "PRIVE" | "PUBLIC";

export interface Societe {
  id: number;
  code: string | null;
  raisonSociale: string;
  ice: string | null;
  adresse: string | null;
  ville: string | null;
  codePostal: string | null;
  telephone: string | null;
  telephone2: string | null;
  fax: string | null;
  codeCommune: string | null;
  email: string | null;
  siteWeb: string | null;
  observation: string | null;
  dateCreation: string | null;
  dateDebutActivite: string | null;
  dateFinActivite: string | null;
  typePersonne: TypePersonne;
  secteur: SecteurSociete;
  loyerAnnuel: number;
  logo: string | null;
  entete: string | null;
  piedDePage: string | null;
  formeJuridique: string | null;
  patente: string | null;
  identifiantFiscal: string | null;
  subdivision: string | null;
  rc: string | null;
  villeRc: string | null;
  cnss: string | null;
  cnss2: string | null;
  rafReception: string | null;
  tauxCm: number;
  tauxImpot: number;
  soumisImpot: boolean;
  activite: string | null;
  banque: string | null;
  agenceBancaire: string | null;
  rib: string | null;
  swift: string | null;
  devise: string;
  capital: number;
  effectif: number;
  autoriserStockNegatif: boolean;
  ppNom: string | null;
  ppPrenom: string | null;
  ppCin: string | null;
  ppQualite: string | null;
}

export type SocieteRequest = Omit<Societe, "id">;

export interface TauxTva {
  id: number;
  taux: number;
  libelle: string;
  actif: boolean;
  parDefaut: boolean;
}

export type TauxTvaRequest = Omit<TauxTva, "id">;

export interface ModeReglement {
  id: number;
  code: TypeReglement;
  libelle: string;
  actif: boolean;
}

export interface NumerotationConfig {
  id: number;
  typeDocument: string;
  libelle: string;
  prefixe: string;
  longueur: number;
  prochainNumero: string;
}
