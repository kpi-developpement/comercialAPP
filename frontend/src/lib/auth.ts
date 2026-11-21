/**
 * Session persistence (JWT + current user) in localStorage. No React here: see features/auth for the context.
 */
import type { CurrentUser } from "@/features/auth/types/auth.types";

const TOKEN_KEY = "gestcom.token";
const USER_KEY = "gestcom.user";
const EXPIRES_KEY = "gestcom.expiresAt";

export interface StoredSession {
  token: string;
  user: CurrentUser;
  expiresAt: string;
}

function isBrowser(): boolean {
  return typeof window !== "undefined";
}

export function saveSession(session: StoredSession): void {
  if (!isBrowser()) return;
  localStorage.setItem(TOKEN_KEY, session.token);
  localStorage.setItem(USER_KEY, JSON.stringify(session.user));
  localStorage.setItem(EXPIRES_KEY, session.expiresAt);
}

export function clearSession(): void {
  if (!isBrowser()) return;
  localStorage.removeItem(TOKEN_KEY);
  localStorage.removeItem(USER_KEY);
  localStorage.removeItem(EXPIRES_KEY);
}

export function getToken(): string | null {
  if (!isBrowser()) return null;
  const expiresAt = localStorage.getItem(EXPIRES_KEY);
  if (expiresAt && new Date(expiresAt).getTime() < Date.now()) {
    clearSession();
    return null;
  }
  return localStorage.getItem(TOKEN_KEY);
}

export function getStoredUser(): CurrentUser | null {
  if (!isBrowser() || !getToken()) return null;
  const raw = localStorage.getItem(USER_KEY);
  if (!raw) return null;
  try {
    return JSON.parse(raw) as CurrentUser;
  } catch {
    return null;
  }
}
