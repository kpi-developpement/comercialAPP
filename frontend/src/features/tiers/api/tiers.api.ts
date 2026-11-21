import { api } from "@/lib/api-client";
import type { ListParams, PageResponse } from "@/lib/types";
import type {
  Client,
  ClientRequest,
  Fournisseur,
  FournisseurRequest,
  TiersKind,
  TiersSummary,
} from "../types/tiers.types";

const base = (kind: TiersKind) => `/api/v1/${kind}`;

export const tiersApi = {
  list: (kind: TiersKind, params: ListParams) => api.get<PageResponse<TiersSummary>>(base(kind), params),
  getClient: (id: number) => api.get<Client>(`${base("clients")}/${id}`),
  getFournisseur: (id: number) => api.get<Fournisseur>(`${base("fournisseurs")}/${id}`),
  saveClient: (id: number | undefined, body: ClientRequest) =>
    id ? api.put<Client>(`${base("clients")}/${id}`, body) : api.post<Client>(base("clients"), body),
  saveFournisseur: (id: number | undefined, body: FournisseurRequest) =>
    id ? api.put<Fournisseur>(`${base("fournisseurs")}/${id}`, body) : api.post<Fournisseur>(base("fournisseurs"), body),
  remove: (kind: TiersKind, id: number) => api.delete(`${base(kind)}/${id}`),
};
