"use client";

import { useQueryClient } from "@tanstack/react-query";
import { createContext, useCallback, useContext, useEffect, useMemo, useState, type ReactNode } from "react";
import { clearSession, getStoredUser, saveSession } from "@/lib/auth";
import { can, type Permission } from "@/lib/permissions";
import { authApi } from "../../api/auth.api";
import type { CurrentUser, LoginRequest } from "../../types/auth.types";

interface AuthContextValue {
  user: CurrentUser | null;
  /** False until the stored session has been read (avoids redirect flicker). */
  ready: boolean;
  login: (credentials: LoginRequest) => Promise<void>;
  logout: () => void;
  can: (permission: Permission) => boolean;
}

const AuthContext = createContext<AuthContextValue | null>(null);

export function AuthProvider({ children }: { children: ReactNode }) {
  const queryClient = useQueryClient();
  const [user, setUser] = useState<CurrentUser | null>(null);
  const [ready, setReady] = useState(false);

  useEffect(() => {
    setUser(getStoredUser());
    setReady(true);
  }, []);

  const login = useCallback(async (credentials: LoginRequest) => {
    const response = await authApi.login(credentials);
    saveSession({ token: response.token, user: response.user, expiresAt: response.expiresAt });
    setUser(response.user);
  }, []);

  const logout = useCallback(() => {
    clearSession();
    queryClient.clear();
    setUser(null);
  }, [queryClient]);

  const value = useMemo<AuthContextValue>(
    () => ({ user, ready, login, logout, can: (p) => can(user?.role, p) }),
    [user, ready, login, logout],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth(): AuthContextValue {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth doit être utilisé dans AuthProvider");
  return ctx;
}
