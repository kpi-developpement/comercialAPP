/**
 * Role matrix mirrored from backend ma.gestcom.security.Roles. Used to hide menu items and actions;
 * the backend remains the authority (@PreAuthorize).
 */
export type Role = "ADMIN" | "COMMERCIAL" | "MAGASINIER" | "COMPTABLE";

export const ROLE_LABELS: Record<Role, string> = {
  ADMIN: "Administrateur",
  COMMERCIAL: "Commercial",
  MAGASINIER: "Magasinier",
  COMPTABLE: "Comptable",
};

export type Permission =
  | "dashboard"
  | "article.write"
  | "client.write"
  | "fournisseur.write"
  | "vente.read"
  | "vente.write"
  | "livraison.write"
  | "achat.read"
  | "achat.write"
  | "achat.reception"
  | "stock.read"
  | "stock.write"
  | "reglement.read"
  | "reglement.write"
  | "admin";

const MATRIX: Record<Permission, Role[]> = {
  dashboard: ["ADMIN", "COMMERCIAL", "COMPTABLE"],
  "article.write": ["ADMIN", "COMMERCIAL", "MAGASINIER"],
  "client.write": ["ADMIN", "COMMERCIAL", "COMPTABLE"],
  "fournisseur.write": ["ADMIN", "COMMERCIAL", "MAGASINIER", "COMPTABLE"],
  "vente.read": ["ADMIN", "COMMERCIAL", "COMPTABLE", "MAGASINIER"],
  "vente.write": ["ADMIN", "COMMERCIAL"],
  "livraison.write": ["ADMIN", "COMMERCIAL", "MAGASINIER"],
  "achat.read": ["ADMIN", "COMMERCIAL", "COMPTABLE", "MAGASINIER"],
  "achat.write": ["ADMIN", "COMMERCIAL"],
  "achat.reception": ["ADMIN", "COMMERCIAL", "MAGASINIER"],
  "stock.read": ["ADMIN", "COMMERCIAL", "MAGASINIER", "COMPTABLE"],
  "stock.write": ["ADMIN", "MAGASINIER"],
  "reglement.read": ["ADMIN", "COMPTABLE", "COMMERCIAL"],
  "reglement.write": ["ADMIN", "COMPTABLE"],
  admin: ["ADMIN"],
};

/** Landing page after login: the dashboard, or the stock status for roles without dashboard access. */
export function homeFor(role: Role | undefined): string {
  return can(role, "dashboard") ? "/dashboard" : "/stock/etat";
}

export function can(role: Role | undefined, permission: Permission): boolean {
  return !!role && MATRIX[permission].includes(role);
}
