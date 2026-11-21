import { api } from "@/lib/api-client";
import type { ListParams, PageResponse } from "@/lib/types";
import type { Utilisateur, UtilisateurCreateRequest, UtilisateurUpdateRequest } from "../types/utilisateur.types";

const BASE = "/api/v1/utilisateurs";

export const utilisateursApi = {
  list: (params: ListParams) => api.get<PageResponse<Utilisateur>>(BASE, params),
  create: (body: UtilisateurCreateRequest) => api.post<Utilisateur>(BASE, body),
  update: (id: number, body: UtilisateurUpdateRequest) => api.put<Utilisateur>(`${BASE}/${id}`, body),
  resetPassword: (id: number, nouveauMotDePasse: string) => api.put<void>(`${BASE}/${id}/mot-de-passe`, { nouveauMotDePasse }),
};
