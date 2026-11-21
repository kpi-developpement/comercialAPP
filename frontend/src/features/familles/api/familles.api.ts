import { api } from "@/lib/api-client";
import type { ListParams, PageResponse } from "@/lib/types";
import type { Famille, FamilleRequest } from "../types/famille.types";

const BASE = "/api/v1/familles";

export const famillesApi = {
  list: (params: ListParams) => api.get<PageResponse<Famille>>(BASE, params),
  all: () => api.get<Famille[]>(`${BASE}/toutes`),
  create: (body: FamilleRequest) => api.post<Famille>(BASE, body),
  update: (id: number, body: FamilleRequest) => api.put<Famille>(`${BASE}/${id}`, body),
  remove: (id: number) => api.delete(`${BASE}/${id}`),
};
