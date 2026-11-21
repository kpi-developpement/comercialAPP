import { z } from "zod";

export const reglementSchema = z
  .object({
    tiersId: z.number().nullable(),
    tiersNom: z.string(),
    dateReglement: z.string().min(1, "Date obligatoire"),
    mode: z.enum(["ESPECES", "CHEQUE", "EFFET", "VIREMENT", "CARTE"]),
    montant: z.number({ error: "Montant attendu" }).gt(0, "Le montant doit être positif"),
    reference: z.string().max(50),
    banque: z.string().max(80),
    dateEcheance: z.string(),
    notes: z.string().max(2000),
  })
  .superRefine((v, ctx) => {
    if (v.tiersId === null) ctx.addIssue({ code: "custom", path: ["tiersId"], message: "Choisissez un tiers" });
    const titre = v.mode === "CHEQUE" || v.mode === "EFFET";
    if (titre && !v.reference.trim()) {
      ctx.addIssue({ code: "custom", path: ["reference"], message: "Numéro obligatoire pour un chèque ou un effet" });
    }
  });

export type ReglementFormValues = z.infer<typeof reglementSchema>;
