import type { Role } from "@/lib/permissions";

export interface CurrentUser {
  id: number;
  username: string;
  nomComplet: string;
  email: string | null;
  role: Role;
  actif: boolean;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  expiresAt: string;
  user: CurrentUser;
}
