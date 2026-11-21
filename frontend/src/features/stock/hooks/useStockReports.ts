"use client";

import { keepPreviousData, useQuery } from "@tanstack/react-query";
import type { ListParams } from "@/lib/types";
import { stockApi } from "../api/stock.api";

export const stockKeys = {
  all: ["stock"] as const,
  etat: (p: ListParams) => ["stock", "etat", p] as const,
  alertes: (p: ListParams) => ["stock", "alertes", p] as const,
  valorisation: ["stock", "valorisation"] as const,
  article: (id: number) => ["stock", "article", id] as const,
  mouvements: (p: ListParams) => ["stock", "mouvements", p] as const,
};

export function useEtatStock(params: ListParams) {
  return useQuery({ queryKey: stockKeys.etat(params), queryFn: () => stockApi.etat(params), placeholderData: keepPreviousData });
}

export function useAlertesStock(params: ListParams) {
  return useQuery({ queryKey: stockKeys.alertes(params), queryFn: () => stockApi.alertes(params), placeholderData: keepPreviousData });
}

export function useValorisation() {
  return useQuery({ queryKey: stockKeys.valorisation, queryFn: stockApi.valorisation });
}

export function useStockArticle(articleId: number) {
  return useQuery({ queryKey: stockKeys.article(articleId), queryFn: () => stockApi.stockArticle(articleId) });
}

export function useMouvements(params: ListParams) {
  return useQuery({
    queryKey: stockKeys.mouvements(params),
    queryFn: () => stockApi.mouvements(params),
    placeholderData: keepPreviousData,
  });
}
