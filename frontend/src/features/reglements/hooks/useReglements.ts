"use client";

import { keepPreviousData, useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import type { ListParams } from "@/lib/types";
import { reglementsApi } from "../api/reglements.api";
import type { AffectationRequest, Reglement, ReglementRequest, Sens, StatutReglement } from "../types/reglement.types";

export const reglementKeys = {
  all: ["reglements"] as const,
  list: (sens: Sens, p: ListParams) => ["reglements", sens, "list", p] as const,
  detail: (sens: Sens, id: number) => ["reglements", sens, "detail", id] as const,
  solde: (sens: Sens, tiersId: number) => ["reglements", sens, "solde", tiersId] as const,
  echeancier: (sens: Sens, p: ListParams) => ["reglements", sens, "echeancier", p] as const,
};

export function useReglements(sens: Sens, params: ListParams) {
  return useQuery({ queryKey: reglementKeys.list(sens, params), queryFn: () => reglementsApi.list(sens, params), placeholderData: keepPreviousData });
}

export function useReglement(sens: Sens, id: number) {
  return useQuery({ queryKey: reglementKeys.detail(sens, id), queryFn: () => reglementsApi.get(sens, id) });
}

export function useSoldeTiers(sens: Sens, tiersId: number | null) {
  return useQuery({
    queryKey: reglementKeys.solde(sens, tiersId ?? 0),
    queryFn: () => reglementsApi.solde(sens, tiersId!),
    enabled: !!tiersId,
  });
}

export function useEcheancier(sens: Sens, params: ListParams) {
  return useQuery({
    queryKey: reglementKeys.echeancier(sens, params),
    queryFn: () => reglementsApi.echeancier(sens, params),
    placeholderData: keepPreviousData,
  });
}

/** Payments change invoice balances: refresh payments, documents and dashboard. */
function useOnPaymentChange(sens: Sens) {
  const qc = useQueryClient();
  return (r?: Reglement) => {
    if (r) qc.setQueryData(reglementKeys.detail(sens, r.id), r);
    qc.invalidateQueries({ queryKey: reglementKeys.all });
    qc.invalidateQueries({ queryKey: ["documents"] });
    qc.invalidateQueries({ queryKey: ["dashboard"] });
  };
}

export function useCreateReglement(sens: Sens) {
  const onChange = useOnPaymentChange(sens);
  return useMutation({ mutationFn: (body: ReglementRequest) => reglementsApi.create(sens, body), onSuccess: onChange });
}

export function useAffecterReglement(sens: Sens, id: number) {
  const onChange = useOnPaymentChange(sens);
  return useMutation({ mutationFn: (a: AffectationRequest[]) => reglementsApi.affecter(sens, id, a), onSuccess: onChange });
}

export function useChangerStatutReglement(sens: Sens, id: number) {
  const onChange = useOnPaymentChange(sens);
  return useMutation({ mutationFn: (s: StatutReglement) => reglementsApi.changerStatut(sens, id, s), onSuccess: onChange });
}

export function useDeleteReglement(sens: Sens) {
  const onChange = useOnPaymentChange(sens);
  return useMutation({ mutationFn: (id: number) => reglementsApi.remove(sens, id), onSuccess: () => onChange() });
}
