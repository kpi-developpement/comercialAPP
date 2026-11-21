import { api } from "@/lib/api-client";
import type { ListParams, PageResponse } from "@/lib/types";
import type {
  Depot,
  DepotRequest,
  EtatStock,
  Inventaire,
  InventaireSummary,
  Mouvement,
  StockParDepot,
  Transfert,
  TransfertRequest,
  Valorisation,
} from "../types/stock.types";

const BASE = "/api/v1/stock";

export const stockApi = {
  depots: (params: ListParams) => api.get<PageResponse<Depot>>(`${BASE}/depots`, params),
  allDepots: () => api.get<Depot[]>(`${BASE}/depots/tous`),
  saveDepot: (id: number | undefined, body: DepotRequest) =>
    id ? api.put<Depot>(`${BASE}/depots/${id}`, body) : api.post<Depot>(`${BASE}/depots`, body),
  deleteDepot: (id: number) => api.delete(`${BASE}/depots/${id}`),

  etat: (params: ListParams) => api.get<PageResponse<EtatStock>>(`${BASE}/etat`, params),
  alertes: (params: ListParams) => api.get<PageResponse<EtatStock>>(`${BASE}/alertes`, params),
  valorisation: () => api.get<Valorisation>(`${BASE}/valorisation`),
  stockArticle: (articleId: number) => api.get<StockParDepot[]>(`${BASE}/articles/${articleId}`),
  mouvements: (params: ListParams) => api.get<PageResponse<Mouvement>>(`${BASE}/mouvements`, params),

  inventaires: (params: ListParams) => api.get<PageResponse<InventaireSummary>>(`${BASE}/inventaires`, params),
  inventaire: (id: number) => api.get<Inventaire>(`${BASE}/inventaires/${id}`),
  createInventaire: (body: { depotId: number; dateInventaire: string; familleId: number | null; notes: string | null }) =>
    api.post<Inventaire>(`${BASE}/inventaires`, body),
  saisirComptage: (id: number, body: { notes: string | null; lignes: { ligneId: number; quantiteComptee: number | null }[] }) =>
    api.put<Inventaire>(`${BASE}/inventaires/${id}/comptage`, body),
  validerInventaire: (id: number) => api.post<Inventaire>(`${BASE}/inventaires/${id}/valider`),
  deleteInventaire: (id: number) => api.delete(`${BASE}/inventaires/${id}`),

  transferts: (params: ListParams) => api.get<PageResponse<Transfert>>(`${BASE}/transferts`, params),
  transfert: (id: number) => api.get<Transfert>(`${BASE}/transferts/${id}`),
  saveTransfert: (id: number | undefined, body: TransfertRequest) =>
    id ? api.put<Transfert>(`${BASE}/transferts/${id}`, body) : api.post<Transfert>(`${BASE}/transferts`, body),
  validerTransfert: (id: number) => api.post<Transfert>(`${BASE}/transferts/${id}/valider`),
  deleteTransfert: (id: number) => api.delete(`${BASE}/transferts/${id}`),
};
