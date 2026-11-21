"use client";

import { keepPreviousData, useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import type { ListParams, Option } from "@/lib/types";
import { stockApi } from "../api/stock.api";
import type { DepotRequest } from "../types/stock.types";

export const depotKeys = {
  all: ["depots"] as const,
  list: (params: ListParams) => ["depots", "list", params] as const,
  options: ["depots", "options"] as const,
};

export function useDepots(params: ListParams) {
  return useQuery({ queryKey: depotKeys.list(params), queryFn: () => stockApi.depots(params), placeholderData: keepPreviousData });
}

export function useAllDepots() {
  return useQuery({ queryKey: depotKeys.options, queryFn: stockApi.allDepots, staleTime: 5 * 60_000 });
}

export function useDepotOptions(activeOnly = true) {
  const query = useAllDepots();
  const options: Option<number>[] = (query.data ?? [])
    .filter((d) => !activeOnly || d.actif)
    .map((d) => ({ value: d.id, label: d.libelle }));
  const defaultId = query.data?.find((d) => d.parDefaut)?.id ?? query.data?.[0]?.id;
  return { options, defaultId, isLoading: query.isLoading };
}

export function useSaveDepot() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({ id, body }: { id?: number; body: DepotRequest }) => stockApi.saveDepot(id, body),
    onSuccess: () => qc.invalidateQueries({ queryKey: depotKeys.all }),
  });
}

export function useDeleteDepot() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (id: number) => stockApi.deleteDepot(id),
    onSuccess: () => qc.invalidateQueries({ queryKey: depotKeys.all }),
  });
}
