import { z } from "zod";

export const familleSchema = z.object({
  code: z.string().trim().min(1, "Code obligatoire").max(30, "30 caractères maximum"),
  libelle: z.string().trim().min(1, "Libellé obligatoire").max(120, "120 caractères maximum"),
  parentId: z.number().nullable(),
});

export type FamilleFormValues = z.infer<typeof familleSchema>;
