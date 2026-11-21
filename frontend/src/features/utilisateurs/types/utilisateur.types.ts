import type { Role } from "@/lib/permissions";

export interface Utilisateur {
  id: number;
  username: string;
  nomComplet: string;
  email: string | null;
  role: Role;
  actif: boolean;
  createdAt: string;
}

export interface UtilisateurCreateRequest {
  username: string;
  nomComplet: string;
  email: string | null;
  role: Role;
  actif: boolean;
  motDePasse: string;
}

export type UtilisateurUpdateRequest = Omit<UtilisateurCreateRequest, "username" | "motDePasse">;
