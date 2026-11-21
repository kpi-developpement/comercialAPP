import { api } from "@/lib/api-client";
import type { ArticleSuggere, CaAnnuel, ProduitFacture, ProduitVendu } from "../types/clientStats.types";

export const clientStatsApi = {
  suggestions: (clientId: number) => api.get<ArticleSuggere[]>(`/api/v1/clients/${clientId}/suggestions`),
  addSuggestion: (clientId: number, articleId: number) =>
    api.post<ArticleSuggere>(`/api/v1/clients/${clientId}/suggestions`, { articleId }),
  removeSuggestion: (clientId: number, id: number) => api.delete(`/api/v1/clients/${clientId}/suggestions/${id}`),
  produits: (clientId: number) => api.get<ProduitVendu[]>(`/api/v1/ventes/clients/${clientId}/produits`),
  facturesProduit: (clientId: number, articleId: number) =>
    api.get<ProduitFacture[]>(`/api/v1/ventes/clients/${clientId}/produits/${articleId}/factures`),
  caAnnuel: (clientId: number, de: number, a: number) =>
    api.get<CaAnnuel[]>(`/api/v1/ventes/clients/${clientId}/ca-annuel`, { de, a }),
};
