"use client";

import { keepPreviousData, useQuery } from "@tanstack/react-query";
import { dashboardApi } from "../api/dashboard.api";

export function useDashboard(dateDebut: string, dateFin: string) {
  return useQuery({
    queryKey: ["dashboard", dateDebut, dateFin],
    queryFn: () => dashboardApi.get(dateDebut, dateFin),
    placeholderData: keepPreviousData,
  });
}
