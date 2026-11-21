import { z } from "zod";
import { blankToNull, nullifyBlanks, str } from "@/lib/form";
import type { Fournisseur, FournisseurRequest } from "../types/tiers.types";

const text = (max: number) => z.string().max(max, `${max} caractères maximum`);
const email = z.union([z.literal(""), z.email("Adresse e-mail invalide")]);
const pct = (label: string) => z.number({ error: `${label} : nombre attendu` }).min(0).max(100, "100 % maximum");
const jours = z.number({ error: "Nombre de jours attendu" }).int("Nombre entier").min(0).max(365, "365 jours maximum");
const montant = z.number({ error: "Montant attendu" }).min(0, "Montant positif ou nul");

export const contactSchema = z.object({
  id: z.number().nullable(),
  nom: z.string().trim().min(1, "Nom obligatoire").max(80),
  prenom: text(80),
  fonction: text(80),
  telephone: text(30),
  email,
});

export const fournisseurSchema = z
  .object({
    code: z.string().trim().min(1, "Code obligatoire").max(30),
    raisonSociale: z.string().trim().min(1, "Raison sociale obligatoire").max(150),
    codeGroupe: text(30),
    typeFournisseur: z.enum(["COMPTANT", "A_TERME"]),
    adresse: z.string().trim().min(1, "Adresse obligatoire").max(255),
    pays: z.string().min(1, "Pays obligatoire").max(60),
    ville: z.string().trim().min(1, "Ville obligatoire").max(80),
    codePostal: text(10),
    nationalite: text(60),
    telephone: text(30),
    telephone2: text(30),
    fax: text(30),
    email,
    siteWeb: text(120),
    activite: text(150),
    devise: z.string().length(3, "Code devise sur 3 lettres"),
    categorie: text(60),
    dateDebutActivite: z.string(),
    dateFinActivite: z.string(),
    notes: text(2000),
    actif: z.boolean(),
    contacts: z.array(contactSchema),
    formeJuridique: text(30),
    rc: z.string().trim().min(1, "RC obligatoire").max(30),
    patente: z.string().trim().min(1, "T.P obligatoire").max(30),
    capital: montant,
    ice: z.string().regex(/^(\d{15})?$/, "L'ICE doit contenir 15 chiffres"),
    identifiantFiscal: text(20),
    cnss: text(30),
    effectif: z.number({ error: "Nombre attendu" }).int().min(0),
    nonResident: z.boolean(),
    commercant: z.boolean(),
    dateCreationSociete: z.string(),
    validationSuperviseur: z.boolean(),
    modeLivraison: text(60),
    delaiLivraisonMin: jours,
    delaiLivraisonMax: jours,
    modeReglementDefaut: z.enum(["ESPECES", "CHEQUE", "EFFET", "VIREMENT", "CARTE"]).nullable(),
    conditionPaiement: text(80),
    commentairePaiement: text(2000),
    plafondDecouvert: montant,
    delaiPaiement: jours,
    plafondRemise: pct("Plafond de remise"),
    remiseDefaut: pct("Remise"),
    escompte: pct("Escompte"),
    imputationTva: text(30),
    codeTva: pct("Code TVA"),
    codeIntegration: text(30),
    attestationNumero: text(50),
    attestationDate: z.string(),
    attestationValidite: z.string(),
  })
  .superRefine((v, ctx) => {
    if (v.delaiLivraisonMax < v.delaiLivraisonMin) {
      ctx.addIssue({ code: "custom", path: ["delaiLivraisonMax"], message: "Doit être ≥ au délai minimum" });
    }
    if (v.attestationDate && v.attestationValidite && v.attestationValidite < v.attestationDate) {
      ctx.addIssue({ code: "custom", path: ["attestationValidite"], message: "Doit être postérieure à la date de l'attestation" });
    }
  });

export type FournisseurFormValues = z.infer<typeof fournisseurSchema>;

export function toFournisseurValues(f: Fournisseur | undefined): FournisseurFormValues {
  return {
    code: f?.code ?? "",
    raisonSociale: f?.raisonSociale ?? "",
    codeGroupe: str(f?.codeGroupe),
    typeFournisseur: f?.typeFournisseur ?? "A_TERME",
    adresse: str(f?.adresse),
    pays: f?.pays ?? "Maroc",
    ville: str(f?.ville),
    codePostal: str(f?.codePostal),
    nationalite: str(f?.nationalite),
    telephone: str(f?.telephone),
    telephone2: str(f?.telephone2),
    fax: str(f?.fax),
    email: str(f?.email),
    siteWeb: str(f?.siteWeb),
    activite: str(f?.activite),
    devise: f?.devise ?? "MAD",
    categorie: str(f?.categorie),
    dateDebutActivite: str(f?.dateDebutActivite),
    dateFinActivite: str(f?.dateFinActivite),
    notes: str(f?.notes),
    actif: f?.actif ?? true,
    contacts: (f?.contacts ?? []).map((c) => ({
      id: c.id, nom: c.nom, prenom: str(c.prenom), fonction: str(c.fonction), telephone: str(c.telephone), email: str(c.email),
    })),
    formeJuridique: str(f?.formeJuridique),
    rc: str(f?.rc),
    patente: str(f?.patente),
    capital: Number(f?.capital ?? 0),
    ice: str(f?.ice),
    identifiantFiscal: str(f?.identifiantFiscal),
    cnss: str(f?.cnss),
    effectif: f?.effectif ?? 0,
    nonResident: f?.nonResident ?? false,
    commercant: f?.commercant ?? false,
    dateCreationSociete: str(f?.dateCreationSociete),
    validationSuperviseur: f?.validationSuperviseur ?? false,
    modeLivraison: str(f?.modeLivraison),
    delaiLivraisonMin: f?.delaiLivraisonMin ?? 0,
    delaiLivraisonMax: f?.delaiLivraisonMax ?? 0,
    modeReglementDefaut: f?.modeReglementDefaut ?? null,
    conditionPaiement: str(f?.conditionPaiement),
    commentairePaiement: str(f?.commentairePaiement),
    plafondDecouvert: Number(f?.plafondDecouvert ?? 0),
    delaiPaiement: f?.delaiPaiement ?? 30,
    plafondRemise: Number(f?.plafondRemise ?? 0),
    remiseDefaut: Number(f?.remiseDefaut ?? 0),
    escompte: Number(f?.escompte ?? 0),
    imputationTva: str(f?.imputationTva),
    codeTva: Number(f?.codeTva ?? 0),
    codeIntegration: str(f?.codeIntegration),
    attestationNumero: str(f?.attestationNumero),
    attestationDate: str(f?.attestationDate),
    attestationValidite: str(f?.attestationValidite),
  };
}

export function toFournisseurRequest(v: FournisseurFormValues): FournisseurRequest {
  const { contacts, ...rest } = v;
  const clean = nullifyBlanks(rest, ["code", "raisonSociale", "pays", "devise"]);
  return {
    ...clean,
    contacts: contacts.map((c) => ({
      id: c.id, nom: c.nom.trim(), prenom: blankToNull(c.prenom), fonction: blankToNull(c.fonction),
      telephone: blankToNull(c.telephone), email: blankToNull(c.email),
    })),
  } as unknown as FournisseurRequest;
}
