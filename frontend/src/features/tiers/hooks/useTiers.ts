"use client";

import { keepPreviousData, useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import type { ListParams } from "@/lib/types";
import { tiersApi } from "../api/tiers.api";
import type { ClientRequest, FournisseurRequest, TiersKind } from "../types/tiers.types";

export const tiersKeys = {
  all: (kind: TiersKind) => ["tiers", kind] as const,
  list: (kind: TiersKind, params: ListParams) => ["tiers", kind, "list", params] as const,
  detail: (kind: TiersKind, id: number) => ["tiers", kind, "detail", id] as const,
};

export function useTiersList(kind: TiersKind, params: ListParams, enabled = true) {
  return useQuery({
    queryKey: tiersKeys.list(kind, params),
    queryFn: () => tiersApi.list(kind, params),
    placeholderData: keepPreviousData,
    enabled,
  });
}

export function useClient(id: number | null | undefined) {
  return useQuery({ queryKey: tiersKeys.detail("clients", id ?? 0), queryFn: () => tiersApi.getClient(id!), enabled: !!id });
}

export function useFournisseur(id: number | null | undefined) {
  return useQuery({
    queryKey: tiersKeys.detail("fournisseurs", id ?? 0),
    queryFn: () => tiersApi.getFournisseur(id!),
    enabled: !!id,
  });
}

/** Common fields of a client or supplier (remise, mode de règlement) for documents and payments. */
export function useTiers(kind: TiersKind, id: number | null | undefined) {
  const client = useClient(kind === "clients" ? id : null);
  const fournisseur = useFournisseur(kind === "fournisseurs" ? id : null);
  return kind === "clients" ? client : fournisseur;
}

export function useSaveClient() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({ id, body }: { id?: number; body: ClientRequest }) => tiersApi.saveClient(id, body),
    onSuccess: (c) => {
      qc.setQueryData(tiersKeys.detail("clients", c.id), c);
      qc.invalidateQueries({ queryKey: [...tiersKeys.all("clients"), "list"] });
    },
  });
}

export function useSaveFournisseur() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({ id, body }: { id?: number; body: FournisseurRequest }) => tiersApi.saveFournisseur(id, body),
    onSuccess: (f) => {
      qc.setQueryData(tiersKeys.detail("fournisseurs", f.id), f);
      qc.invalidateQueries({ queryKey: [...tiersKeys.all("fournisseurs"), "list"] });
    },
  });
}

export function useDeleteTiers(kind: TiersKind) {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (id: number) => tiersApi.remove(kind, id),
    onSuccess: () => qc.invalidateQueries({ queryKey: tiersKeys.all(kind) }),
  });
}
