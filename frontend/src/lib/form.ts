/** Helpers shared by forms: the API expects null for empty optional text and dates. */

export function blankToNull(value: string | null | undefined): string | null {
  return value === null || value === undefined || value.trim() === "" ? null : value.trim();
}

/** Applies blankToNull to every string property of an object (numbers / booleans untouched). */
export function nullifyBlanks<T extends Record<string, unknown>>(values: T, keep: (keyof T)[] = []): T {
  const out: Record<string, unknown> = { ...values };
  for (const [key, value] of Object.entries(values)) {
    if (typeof value === "string" && !keep.includes(key as keyof T)) out[key] = blankToNull(value);
  }
  return out as T;
}

/** API value -> input value ("" for null). */
export function str(value: string | null | undefined): string {
  return value ?? "";
}

export const PAYS = ["Maroc", "France", "Espagne", "Belgique", "Algérie", "Tunisie", "Mauritanie", "Sénégal", "Émirats arabes unis", "Autre"];
export const DEVISES = ["MAD", "EUR", "USD", "GBP", "AED"];
export const FORMES_JURIDIQUES = ["SARL", "SARL AU", "SA", "SAS", "SNC", "Personne physique", "Coopérative", "Association", "Autre"];
export const MODES_LIVRAISON = ["Enlèvement client", "Livraison par nos soins", "Transporteur", "Messagerie", "Autre"];
