"use client";

import { keepPreviousData, useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import type { ListParams } from "@/lib/types";
import { stockApi } from "../api/stock.api";
import type { TransfertRequest } from "../types/stock.types";
import { stockKeys } from "./useStockReports";

const keys = {
  all: ["transferts"] as const,
  list: (p: ListParams) => ["transferts", "list", p] as const,
  detail: (id: number) => ["transferts", "detail", id] as const,
};

export function useTransferts(params: ListParams) {
  return useQuery({ queryKey: keys.list(params), queryFn: () => stockApi.transferts(params), placeholderData: keepPreviousData });
}

export function useTransfert(id: number | undefined) {
  return useQuery({ queryKey: keys.detail(id ?? 0), queryFn: () => stockApi.transfert(id!), enabled: !!id });
}

export function useSaveTransfert() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({ id, body }: { id?: number; body: TransfertRequest }) => stockApi.saveTransfert(id, body),
    onSuccess: (data) => {
      qc.setQueryData(keys.detail(data.id), data);
      qc.invalidateQueries({ queryKey: [...keys.all, "list"] });
    },
  });
}

export function useValiderTransfert() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (id: number) => stockApi.validerTransfert(id),
    onSuccess: (data) => {
      qc.setQueryData(keys.detail(data.id), data);
      qc.invalidateQueries({ queryKey: [...keys.all, "list"] });
      qc.invalidateQueries({ queryKey: stockKeys.all });
    },
  });
}

export function useDeleteTransfert() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (id: number) => stockApi.deleteTransfert(id),
    onSuccess: () => qc.invalidateQueries({ queryKey: keys.all }),
  });
}
