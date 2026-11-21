"use client";

import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { parametresApi } from "../api/parametres.api";
import type { SocieteRequest, TauxTvaRequest } from "../types/parametre.types";

export const parametresKeys = {
  societe: ["parametres", "societe"] as const,
  tva: ["parametres", "tva"] as const,
  modes: ["parametres", "modes"] as const,
  numerotation: ["parametres", "numerotation"] as const,
};

export function useSociete() {
  return useQuery({ queryKey: parametresKeys.societe, queryFn: parametresApi.getSociete, staleTime: 5 * 60_000 });
}

export function useUpdateSociete() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (body: SocieteRequest) => parametresApi.updateSociete(body),
    onSuccess: (data) => qc.setQueryData(parametresKeys.societe, data),
  });
}

export function useTauxTva() {
  return useQuery({ queryKey: parametresKeys.tva, queryFn: parametresApi.listTauxTva, staleTime: 5 * 60_000 });
}

export function useSaveTauxTva() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({ id, body }: { id?: number; body: TauxTvaRequest }) =>
      id ? parametresApi.updateTauxTva(id, body) : parametresApi.createTauxTva(body),
    onSuccess: () => qc.invalidateQueries({ queryKey: parametresKeys.tva }),
  });
}

export function useDeleteTauxTva() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (id: number) => parametresApi.deleteTauxTva(id),
    onSuccess: () => qc.invalidateQueries({ queryKey: parametresKeys.tva }),
  });
}

export function useModesReglement() {
  return useQuery({ queryKey: parametresKeys.modes, queryFn: parametresApi.listModes, staleTime: 5 * 60_000 });
}

export function useUpdateModeReglement() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({ id, libelle, actif }: { id: number; libelle: string; actif: boolean }) =>
      parametresApi.updateMode(id, { libelle, actif }),
    onSuccess: () => qc.invalidateQueries({ queryKey: parametresKeys.modes }),
  });
}

export function useNumerotation() {
  return useQuery({ queryKey: parametresKeys.numerotation, queryFn: parametresApi.listNumerotation });
}

export function useUpdateNumerotation() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({ type, prefixe, longueur }: { type: string; prefixe: string; longueur: number }) =>
      parametresApi.updateNumerotation(type, { prefixe, longueur }),
    onSuccess: () => qc.invalidateQueries({ queryKey: parametresKeys.numerotation }),
  });
}
