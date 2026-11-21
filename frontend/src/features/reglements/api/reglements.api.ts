import type { DocumentSummary } from "@/features/documents/types/document.types";
import { api } from "@/lib/api-client";
import type { ListParams, PageResponse } from "@/lib/types";
import type { AffectationRequest, Reglement, ReglementRequest, Sens, SoldeTiers, StatutReglement } from "../types/reglement.types";

const base = (sens: Sens) => `/api/v1/reglements/${sens}`;

export const reglementsApi = {
  list: (sens: Sens, params: ListParams) => api.get<PageResponse<Reglement>>(base(sens), params),
  get: (sens: Sens, id: number) => api.get<Reglement>(`${base(sens)}/${id}`),
  create: (sens: Sens, body: ReglementRequest) => api.post<Reglement>(base(sens), body),
  affecter: (sens: Sens, id: number, affectations: AffectationRequest[]) =>
    api.post<Reglement>(`${base(sens)}/${id}/affectations`, { affectations }),
  changerStatut: (sens: Sens, id: number, statut: StatutReglement) =>
    api.put<Reglement>(`${base(sens)}/${id}/statut`, { statut }),
  remove: (sens: Sens, id: number) => api.delete(`${base(sens)}/${id}`),
  solde: (sens: Sens, tiersId: number) => api.get<SoldeTiers>(`${base(sens)}/soldes/${tiersId}`),
  echeancier: (sens: Sens, params: ListParams) => api.get<PageResponse<DocumentSummary>>(`${base(sens)}/echeancier`, params),
};
