"use client";

import { keepPreviousData, useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { clientStatsApi } from "../api/clientStats.api";

const keys = {
  suggestions: (id: number) => ["clients", id, "suggestions"] as const,
  produits: (id: number) => ["clients", id, "produits"] as const,
  factures: (id: number, articleId: number) => ["clients", id, "produits", articleId] as const,
  ca: (id: number, de: number, a: number) => ["clients", id, "ca", de, a] as const,
};

export function useSuggestions(clientId: number) {
  return useQuery({ queryKey: keys.suggestions(clientId), queryFn: () => clientStatsApi.suggestions(clientId) });
}

export function useAddSuggestion(clientId: number) {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (articleId: number) => clientStatsApi.addSuggestion(clientId, articleId),
    onSuccess: () => qc.invalidateQueries({ queryKey: keys.suggestions(clientId) }),
  });
}

export function useRemoveSuggestion(clientId: number) {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (id: number) => clientStatsApi.removeSuggestion(clientId, id),
    onSuccess: () => qc.invalidateQueries({ queryKey: keys.suggestions(clientId) }),
  });
}

export function useProduitsVendus(clientId: number) {
  return useQuery({ queryKey: keys.produits(clientId), queryFn: () => clientStatsApi.produits(clientId) });
}

export function useFacturesProduit(clientId: number, articleId: number | null) {
  return useQuery({
    queryKey: keys.factures(clientId, articleId ?? 0),
    queryFn: () => clientStatsApi.facturesProduit(clientId, articleId!),
    enabled: !!articleId,
  });
}

export function useCaAnnuel(clientId: number, de: number, a: number) {
  return useQuery({
    queryKey: keys.ca(clientId, de, a),
    queryFn: () => clientStatsApi.caAnnuel(clientId, de, a),
    placeholderData: keepPreviousData,
    enabled: de > 1900 && a >= de,
  });
}
