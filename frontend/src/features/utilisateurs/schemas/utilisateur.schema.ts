import { z } from "zod";

const base = {
  nomComplet: z.string().trim().min(1, "Nom obligatoire").max(120),
  email: z.union([z.literal(""), z.email("Adresse e-mail invalide")]),
  role: z.enum(["ADMIN", "COMMERCIAL", "MAGASINIER", "COMPTABLE"]),
  actif: z.boolean(),
};

export const utilisateurCreateSchema = z.object({
  ...base,
  username: z.string().trim().min(3, "3 caractères minimum").max(50).regex(/^[a-zA-Z0-9._-]+$/, "Lettres, chiffres, . _ - uniquement"),
  motDePasse: z.string().min(8, "8 caractères minimum"),
});

export const utilisateurUpdateSchema = z.object({
  ...base,
  username: z.string(),
  motDePasse: z.string(),
});

export type UtilisateurFormValues = z.infer<typeof utilisateurCreateSchema>;
