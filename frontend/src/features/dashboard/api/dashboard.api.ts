import { api } from "@/lib/api-client";
import type { Dashboard } from "../types/dashboard.types";

export const dashboardApi = {
  get: (dateDebut: string, dateFin: string) => api.get<Dashboard>("/api/v1/dashboard", { dateDebut, dateFin }),
};
