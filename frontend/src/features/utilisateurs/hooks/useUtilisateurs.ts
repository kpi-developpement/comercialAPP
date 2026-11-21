"use client";

import { keepPreviousData, useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import type { ListParams } from "@/lib/types";
import { utilisateursApi } from "../api/utilisateurs.api";
import type { UtilisateurCreateRequest, UtilisateurUpdateRequest } from "../types/utilisateur.types";

const keys = { all: ["utilisateurs"] as const, list: (p: ListParams) => ["utilisateurs", "list", p] as const };

export function useUtilisateurs(params: ListParams) {
  return useQuery({ queryKey: keys.list(params), queryFn: () => utilisateursApi.list(params), placeholderData: keepPreviousData });
}

export function useCreateUtilisateur() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (body: UtilisateurCreateRequest) => utilisateursApi.create(body),
    onSuccess: () => qc.invalidateQueries({ queryKey: keys.all }),
  });
}

export function useUpdateUtilisateur() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({ id, body }: { id: number; body: UtilisateurUpdateRequest }) => utilisateursApi.update(id, body),
    onSuccess: () => qc.invalidateQueries({ queryKey: keys.all }),
  });
}

export function useResetPassword() {
  return useMutation({
    mutationFn: ({ id, password }: { id: number; password: string }) => utilisateursApi.resetPassword(id, password),
  });
}
