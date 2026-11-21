import { api } from "@/lib/api-client";
import type { ListParams, PageResponse } from "@/lib/types";
import type { Article, ArticleRequest, ArticleSummary } from "../types/article.types";

const BASE = "/api/v1/articles";

export const articlesApi = {
  list: (params: ListParams) => api.get<PageResponse<ArticleSummary>>(BASE, params),
  get: (id: number) => api.get<Article>(`${BASE}/${id}`),
  scan: (valeur: string) => api.get<Article>(`${BASE}/scan`, { valeur }),
  create: (body: ArticleRequest) => api.post<Article>(BASE, body),
  update: (id: number, body: ArticleRequest) => api.put<Article>(`${BASE}/${id}`, body),
  remove: (id: number) => api.delete(`${BASE}/${id}`),
};
