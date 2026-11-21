/**
 * Typed fetch wrapper: base URL, JWT header, JSON (de)serialization and ApiError parsing.
 * Feature api files call only these helpers.
 */
import { clearSession, getToken } from "./auth";

export const API_URL = (process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:3255").replace(/\/$/, "");

export interface ApiFieldError {
  field: string;
  message: string;
}

interface ApiErrorBody {
  status: number;
  code: string;
  message: string;
  errors?: ApiFieldError[];
}

export class ApiError extends Error {
  readonly status: number;
  readonly code: string;
  readonly fieldErrors: ApiFieldError[];

  constructor(status: number, code: string, message: string, fieldErrors: ApiFieldError[] = []) {
    super(message);
    this.status = status;
    this.code = code;
    this.fieldErrors = fieldErrors;
  }
}

export type QueryValue = string | number | boolean | null | undefined;
export type QueryParams = Record<string, QueryValue>;

interface RequestOptions {
  method?: "GET" | "POST" | "PUT" | "DELETE";
  body?: unknown;
  query?: QueryParams;
  signal?: AbortSignal;
}

export function buildQuery(query?: QueryParams): string {
  if (!query) return "";
  const params = new URLSearchParams();
  Object.entries(query).forEach(([key, value]) => {
    if (value !== undefined && value !== null && value !== "") {
      params.append(key, String(value));
    }
  });
  const s = params.toString();
  return s ? `?${s}` : "";
}

async function send(path: string, options: RequestOptions): Promise<Response> {
  const headers: Record<string, string> = { Accept: "application/json" };
  const token = getToken();
  if (token) headers.Authorization = `Bearer ${token}`;
  if (options.body !== undefined) headers["Content-Type"] = "application/json";

  let response: Response;
  try {
    response = await fetch(`${API_URL}${path}${buildQuery(options.query)}`, {
      method: options.method ?? "GET",
      headers,
      body: options.body !== undefined ? JSON.stringify(options.body) : undefined,
      signal: options.signal,
    });
  } catch {
    throw new ApiError(0, "NETWORK_ERROR", "Le serveur est injoignable. Vérifiez votre connexion puis réessayez.");
  }

  if (response.status === 401 && !path.startsWith("/api/v1/auth/login")) {
    clearSession();
    if (typeof window !== "undefined" && !window.location.pathname.startsWith("/login")) {
      window.location.href = "/login?expired=1";
    }
  }
  if (!response.ok) {
    throw await toApiError(response);
  }
  return response;
}

async function toApiError(response: Response): Promise<ApiError> {
  try {
    const body = (await response.json()) as ApiErrorBody;
    return new ApiError(response.status, body.code, body.message, body.errors ?? []);
  } catch {
    return new ApiError(response.status, "HTTP_" + response.status, "Une erreur inattendue est survenue.");
  }
}

export async function apiFetch<T>(path: string, options: RequestOptions = {}): Promise<T> {
  const response = await send(path, options);
  if (response.status === 204) return undefined as T;
  const text = await response.text();
  return (text ? JSON.parse(text) : undefined) as T;
}

export async function apiBlob(path: string): Promise<Blob> {
  const response = await send(path, {});
  return response.blob();
}

export const api = {
  get: <T>(path: string, query?: QueryParams) => apiFetch<T>(path, { query }),
  post: <T>(path: string, body?: unknown) => apiFetch<T>(path, { method: "POST", body: body ?? {} }),
  put: <T>(path: string, body: unknown) => apiFetch<T>(path, { method: "PUT", body }),
  delete: (path: string) => apiFetch<void>(path, { method: "DELETE" }),
};

/** Human readable message for any thrown value. */
export function errorMessage(error: unknown): string {
  if (error instanceof ApiError) return error.message;
  if (error instanceof Error) return error.message;
  return "Une erreur inattendue est survenue.";
}
