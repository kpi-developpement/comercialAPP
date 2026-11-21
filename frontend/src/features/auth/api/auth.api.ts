import { api } from "@/lib/api-client";
import type { CurrentUser, LoginRequest, LoginResponse } from "../types/auth.types";

export const authApi = {
  login: (body: LoginRequest) => api.post<LoginResponse>("/api/v1/auth/login", body),
  me: () => api.get<CurrentUser>("/api/v1/auth/me"),
  changePassword: (ancienMotDePasse: string, nouveauMotDePasse: string) =>
    api.put<void>("/api/v1/auth/mot-de-passe", { ancienMotDePasse, nouveauMotDePasse }),
};
