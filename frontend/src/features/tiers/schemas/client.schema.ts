import { z } from "zod";
import { nullifyBlanks, str } from "@/lib/form";
import type { Client, ClientRequest } from "../types/tiers.types";

const text = (max: number) => z.string().max(max, `${max} caractères maximum`);
const email = z.union([z.literal(""), z.email("Adresse e-mail invalide")]);
const pct = (label: string) => z.number({ error: `${label} : nombre attendu` }).min(0).max(100, "100 % maximum");

export const clientSchema = z.object({
  code: z.string().trim().min(1, "Code obligatoire").max(30),
  raisonSociale: z.string().trim().min(1, "Raison sociale obligatoire").max(150),
  nature: z.enum(["CLIENT", "PROSPECT"]),
  bloque: z.boolean(),
  arbitrage: z.boolean(),
  groupeSocietes: z.boolean(),
  codeGroupe: text(30),
  typeClient: z.enum(["COMPTANT", "DEPOSITAIRE", "A_TERME", "AUTRE"]),
  risque: z.enum(["NORMAL", "LITIGIEUX", "RISQUE"]),
  ice: z.string().regex(/^(\d{15})?$/, "L'ICE doit contenir 15 chiffres"),
  identifiantFiscal: text(20),
  rc: text(30),
  patente: text(30),
  cnss: text(30),
  cin: text(20),
  adresse: text(255),
  pays: z.string().min(1, "Pays obligatoire").max(60),
  ville: text(80),
  codePostal: text(10),
  telephone: text(30),
  telephone2: text(30),
  fax: text(30),
  email,
  siteWeb: text(120),
  devise: z.string().length(3, "Code devise sur 3 lettres"),
  representant: text(80),
  depotId: z.number().nullable(),
  activite: text(150),
  categorie: text(60),
  journal: text(30),
  compteComptable: text(30),
  emailRelance: email,
  modeLivraison: text(60),
  modeReglementDefaut: z.enum(["ESPECES", "CHEQUE", "EFFET", "VIREMENT", "CARTE"]).nullable(),
  delaiPaiement: z.number({ error: "Nombre de jours attendu" }).int("Nombre entier").min(0).max(365, "365 jours maximum"),
  remiseDefaut: pct("Remise"),
  escompte: pct("Escompte"),
  plafondCredit: z.number({ error: "Montant attendu" }).min(0, "Montant positif ou nul"),
  dateDebutProspect: z.string(),
  dateDebutActivite: z.string(),
  dateFinActivite: z.string(),
  dateCreationSociete: z.string(),
  dateFinSociete: z.string(),
  notes: text(2000),
  actif: z.boolean(),
});

export type ClientFormValues = z.infer<typeof clientSchema>;

export function toClientValues(c: Client | undefined): ClientFormValues {
  return {
    code: c?.code ?? "",
    raisonSociale: c?.raisonSociale ?? "",
    nature: c?.prospect ? "PROSPECT" : "CLIENT",
    bloque: c?.bloque ?? false,
    arbitrage: c?.arbitrage ?? false,
    groupeSocietes: c?.groupeSocietes ?? false,
    codeGroupe: str(c?.codeGroupe),
    typeClient: c?.typeClient ?? "A_TERME",
    risque: c?.risque ?? "NORMAL",
    ice: str(c?.ice),
    identifiantFiscal: str(c?.identifiantFiscal),
    rc: str(c?.rc),
    patente: str(c?.patente),
    cnss: str(c?.cnss),
    cin: str(c?.cin),
    adresse: str(c?.adresse),
    pays: c?.pays ?? "Maroc",
    ville: str(c?.ville),
    codePostal: str(c?.codePostal),
    telephone: str(c?.telephone),
    telephone2: str(c?.telephone2),
    fax: str(c?.fax),
    email: str(c?.email),
    siteWeb: str(c?.siteWeb),
    devise: c?.devise ?? "MAD",
    representant: str(c?.representant),
    depotId: c?.depotId ?? null,
    activite: str(c?.activite),
    categorie: str(c?.categorie),
    journal: str(c?.journal),
    compteComptable: str(c?.compteComptable),
    emailRelance: str(c?.emailRelance),
    modeLivraison: str(c?.modeLivraison),
    modeReglementDefaut: c?.modeReglementDefaut ?? null,
    delaiPaiement: c?.delaiPaiement ?? 30,
    remiseDefaut: Number(c?.remiseDefaut ?? 0),
    escompte: Number(c?.escompte ?? 0),
    plafondCredit: Number(c?.plafondCredit ?? 0),
    dateDebutProspect: str(c?.dateDebutProspect),
    dateDebutActivite: str(c?.dateDebutActivite),
    dateFinActivite: str(c?.dateFinActivite),
    dateCreationSociete: str(c?.dateCreationSociete),
    dateFinSociete: str(c?.dateFinSociete),
    notes: str(c?.notes),
    actif: c?.actif ?? true,
  };
}

/** Form values -> API payload (blank strings become null). */
export function toClientRequest(v: ClientFormValues): ClientRequest {
  const { nature, ...rest } = v;
  const clean = nullifyBlanks(rest, ["code", "raisonSociale", "pays", "devise"]);
  return { ...clean, prospect: nature === "PROSPECT" } as unknown as ClientRequest;
}
