import { z } from "zod";

const amount = (label: string) =>
  z.number({ error: `${label} : nombre attendu` }).min(0, `${label} doit être positif ou nul`);

export const articleSchema = z.object({
  code: z.string().trim().min(1, "Code obligatoire").max(30, "30 caractères maximum"),
  designation: z.string().trim().min(1, "Désignation obligatoire").max(200, "200 caractères maximum"),
  familleId: z.number().nullable(),
  unite: z.string().trim().min(1, "Unité obligatoire").max(20, "20 caractères maximum"),
  prixAchatHt: amount("Prix d'achat"),
  prixVenteHt: amount("Prix de vente"),
  tauxTva: z.number({ error: "Choisissez un taux" }),
  codeBarres: z.string().trim().max(50, "50 caractères maximum").nullable(),
  stockMinimum: amount("Stock minimum"),
  actif: z.boolean(),
  type: z.enum(["STOCKE", "SERVICE"]),
});

export type ArticleFormValues = z.infer<typeof articleSchema>;
