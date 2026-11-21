import { api } from "@/lib/api-client";
import type {
  ModeReglement,
  NumerotationConfig,
  Societe,
  SocieteRequest,
  TauxTva,
  TauxTvaRequest,
} from "../types/parametre.types";

const BASE = "/api/v1/parametres";

export const parametresApi = {
  getSociete: () => api.get<Societe>(`${BASE}/societe`),
  updateSociete: (body: SocieteRequest) => api.put<Societe>(`${BASE}/societe`, body),

  listTauxTva: () => api.get<TauxTva[]>(`${BASE}/taux-tva`),
  createTauxTva: (body: TauxTvaRequest) => api.post<TauxTva>(`${BASE}/taux-tva`, body),
  updateTauxTva: (id: number, body: TauxTvaRequest) => api.put<TauxTva>(`${BASE}/taux-tva/${id}`, body),
  deleteTauxTva: (id: number) => api.delete(`${BASE}/taux-tva/${id}`),

  listModes: () => api.get<ModeReglement[]>(`${BASE}/modes-reglement`),
  updateMode: (id: number, body: { libelle: string; actif: boolean }) =>
    api.put<ModeReglement>(`${BASE}/modes-reglement/${id}`, body),

  listNumerotation: () => api.get<NumerotationConfig[]>(`${BASE}/numerotation`),
  updateNumerotation: (type: string, body: { prefixe: string; longueur: number }) =>
    api.put<NumerotationConfig>(`${BASE}/numerotation/${type}`, body),
};
