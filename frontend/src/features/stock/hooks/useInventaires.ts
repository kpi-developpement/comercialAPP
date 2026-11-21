"use client";

import { keepPreviousData, useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import type { ListParams } from "@/lib/types";
import { stockApi } from "../api/stock.api";
import { stockKeys } from "./useStockReports";

const keys = {
  all: ["inventaires"] as const,
  list: (p: ListParams) => ["inventaires", "list", p] as const,
  detail: (id: number) => ["inventaires", "detail", id] as const,
};

export function useInventaires(params: ListParams) {
  return useQuery({ queryKey: keys.list(params), queryFn: () => stockApi.inventaires(params), placeholderData: keepPreviousData });
}

export function useInventaire(id: number) {
  return useQuery({ queryKey: keys.detail(id), queryFn: () => stockApi.inventaire(id) });
}

export function useCreateInventaire() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: stockApi.createInventaire,
    onSuccess: () => qc.invalidateQueries({ queryKey: keys.all }),
  });
}

export function useSaisirComptage(id: number) {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (body: Parameters<typeof stockApi.saisirComptage>[1]) => stockApi.saisirComptage(id, body),
    onSuccess: (data) => qc.setQueryData(keys.detail(id), data),
  });
}

export function useValiderInventaire(id: number) {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: () => stockApi.validerInventaire(id),
    onSuccess: (data) => {
      qc.setQueryData(keys.detail(id), data);
      qc.invalidateQueries({ queryKey: keys.all });
      qc.invalidateQueries({ queryKey: stockKeys.all });
    },
  });
}

export function useDeleteInventaire() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (id: number) => stockApi.deleteInventaire(id),
    onSuccess: () => qc.invalidateQueries({ queryKey: keys.all }),
  });
}
