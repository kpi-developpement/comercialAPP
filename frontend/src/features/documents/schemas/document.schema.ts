import { z } from "zod";

export const documentLineSchema = z.object({
  articleId: z.number().nullable(),
  articleCode: z.string(),
  designation: z.string().trim().min(1, "Désignation obligatoire").max(255),
  unite: z.string(),
  quantite: z.number({ error: "Quantité attendue" }).gt(0, "Quantité > 0"),
  prixUnitaireHt: z.number({ error: "Prix attendu" }).min(0, "Prix ≥ 0"),
  remisePct: z.number({ error: "Remise attendue" }).min(0, "≥ 0").max(100, "≤ 100"),
  tauxTva: z.number({ error: "TVA attendue" }).min(0).max(100),
  sourceLigneId: z.number().nullable(),
  articleStocke: z.boolean(),
});

export const documentSchema = z.object({
  tiersId: z.number().nullable(),
  tiersNom: z.string(),
  dateDocument: z.string().min(1, "Date obligatoire"),
  depotId: z.number().nullable(),
  notes: z.string().max(2000),
  dateEcheance: z.string(),
  dateValidite: z.string(),
  retourMarchandise: z.boolean(),
  referenceFournisseur: z.string().max(50),
  lignes: z.array(documentLineSchema).min(1, "Ajoutez au moins une ligne"),
}).superRefine((v, ctx) => {
  if (v.tiersId === null) ctx.addIssue({ code: "custom", path: ["tiersId"], message: "Choisissez un tiers" });
  if (v.depotId === null) ctx.addIssue({ code: "custom", path: ["depotId"], message: "Choisissez un dépôt" });
});

export type DocumentLineValues = z.infer<typeof documentLineSchema>;
export type DocumentFormValues = z.infer<typeof documentSchema>;

export const EMPTY_LINE: DocumentLineValues = {
  articleId: null,
  articleCode: "",
  designation: "",
  unite: "",
  quantite: 1,
  prixUnitaireHt: 0,
  remisePct: 0,
  tauxTva: 20,
  sourceLigneId: null,
  articleStocke: false,
};
