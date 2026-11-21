"use client";

import { keepPreviousData, useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import type { ListParams, Option } from "@/lib/types";
import { famillesApi } from "../api/familles.api";
import type { FamilleRequest } from "../types/famille.types";

export const familleKeys = {
  all: ["familles"] as const,
  list: (params: ListParams) => ["familles", "list", params] as const,
  options: ["familles", "options"] as const,
};

export function useFamilles(params: ListParams) {
  return useQuery({
    queryKey: familleKeys.list(params),
    queryFn: () => famillesApi.list(params),
    placeholderData: keepPreviousData,
  });
}

/** Every family as select options, sous-familles indented under their parent. */
export function useFamilleOptions() {
  return useQuery({
    queryKey: familleKeys.options,
    queryFn: famillesApi.all,
    select: (familles): Option<number>[] => {
      const roots = familles.filter((f) => f.parentId === null);
      return roots.flatMap((root) => [
        { value: root.id, label: root.libelle },
        ...familles
          .filter((f) => f.parentId === root.id)
          .map((child) => ({ value: child.id, label: `— ${child.libelle}` })),
      ]);
    },
  });
}

export function useRootFamilleOptions(excludeId?: number) {
  return useQuery({
    queryKey: familleKeys.options,
    queryFn: famillesApi.all,
    select: (familles): Option<number>[] =>
      familles.filter((f) => f.parentId === null && f.id !== excludeId).map((f) => ({ value: f.id, label: f.libelle })),
  });
}

export function useSaveFamille() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({ id, body }: { id?: number; body: FamilleRequest }) =>
      id ? famillesApi.update(id, body) : famillesApi.create(body),
    onSuccess: () => qc.invalidateQueries({ queryKey: familleKeys.all }),
  });
}

export function useDeleteFamille() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (id: number) => famillesApi.remove(id),
    onSuccess: () => qc.invalidateQueries({ queryKey: familleKeys.all }),
  });
}
