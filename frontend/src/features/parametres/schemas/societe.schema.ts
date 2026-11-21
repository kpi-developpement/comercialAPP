import { z } from "zod";
import { nullifyBlanks, str } from "@/lib/form";
import type { Societe, SocieteRequest } from "../types/parametre.types";

const text = (max: number) => z.string().max(max, `${max} caractères maximum`);
const pct = z.number({ error: "Pourcentage attendu" }).min(0).max(100, "100 % maximum");
const montant = z.number({ error: "Montant attendu" }).min(0, "Montant positif ou nul");

export const societeSchema = z.object({
  code: text(20),
  raisonSociale: z.string().trim().min(1, "Nom de la société obligatoire").max(150),
  ice: z.string().regex(/^(\d{15})?$/, "L'ICE doit contenir 15 chiffres"),
  adresse: text(255),
  ville: text(80),
  codePostal: text(10),
  telephone: text(30),
  telephone2: text(30),
  fax: text(30),
  codeCommune: text(20),
  email: z.union([z.literal(""), z.email("Adresse e-mail invalide")]),
  siteWeb: text(120),
  observation: text(2000),
  dateCreation: z.string(),
  dateDebutActivite: z.string(),
  dateFinActivite: z.string(),
  typePersonne: z.enum(["MORALE", "PHYSIQUE"]),
  secteur: z.enum(["PRIVE", "PUBLIC"]),
  loyerAnnuel: montant,
  logo: z.string().nullable(),
  entete: text(2000),
  piedDePage: text(2000),
  formeJuridique: text(30),
  patente: text(30),
  identifiantFiscal: text(20),
  subdivision: text(60),
  rc: text(30),
  villeRc: text(80),
  cnss: text(30),
  cnss2: text(30),
  rafReception: text(60),
  tauxCm: pct,
  tauxImpot: pct,
  soumisImpot: z.boolean(),
  activite: text(150),
  banque: text(80),
  agenceBancaire: text(80),
  rib: z.string().regex(/^(\d{24})?$/, "Le RIB doit contenir 24 chiffres"),
  swift: text(20),
  devise: z.string().length(3, "Code devise sur 3 lettres"),
  capital: montant,
  effectif: z.number({ error: "Nombre attendu" }).int().min(0),
  autoriserStockNegatif: z.boolean(),
  ppNom: text(80),
  ppPrenom: text(80),
  ppCin: text(20),
  ppQualite: text(80),
});

export type SocieteFormValues = z.infer<typeof societeSchema>;

const NUMBERS = ["loyerAnnuel", "tauxCm", "tauxImpot", "capital", "effectif"] as const;
const BOOLEANS = ["soumisImpot", "autoriserStockNegatif"] as const;

export function toSocieteValues(s: Societe): SocieteFormValues {
  const out: Record<string, unknown> = {};
  for (const key of Object.keys(societeSchema.shape) as (keyof SocieteFormValues)[]) {
    const value = s[key as keyof Societe];
    if ((NUMBERS as readonly string[]).includes(key)) out[key] = Number(value ?? 0);
    else if ((BOOLEANS as readonly string[]).includes(key)) out[key] = Boolean(value);
    else if (key === "logo") out[key] = s.logo;
    else out[key] = typeof value === "string" ? value : str(value as string | null);
  }
  out.typePersonne = s.typePersonne ?? "MORALE";
  out.secteur = s.secteur ?? "PRIVE";
  out.devise = s.devise ?? "MAD";
  return out as SocieteFormValues;
}

export function toSocieteRequest(v: SocieteFormValues): SocieteRequest {
  return nullifyBlanks(v, ["raisonSociale", "devise"]) as unknown as SocieteRequest;
}
