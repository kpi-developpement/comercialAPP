/**
 * Display formats: money "1 234,56", quantities "2,5", dates "21/11/2026".
 */
const moneyFormatter = new Intl.NumberFormat("fr-FR", { minimumFractionDigits: 2, maximumFractionDigits: 2 });
const qtyFormatter = new Intl.NumberFormat("fr-FR", { maximumFractionDigits: 3 });
const pctFormatter = new Intl.NumberFormat("fr-FR", { maximumFractionDigits: 2 });

/** Replaces the narrow no-break space used by Intl with a plain space for readability in tables. */
function normalize(s: string): string {
  return s.replace(/ | /g, " ");
}

export function formatMoney(value: number | string | null | undefined, currency?: string): string {
  const n = Number(value ?? 0);
  const s = normalize(moneyFormatter.format(Number.isFinite(n) ? n : 0));
  return currency ? `${s} ${currency}` : s;
}

export function formatQty(value: number | string | null | undefined): string {
  const n = Number(value ?? 0);
  return normalize(qtyFormatter.format(Number.isFinite(n) ? n : 0));
}

export function formatPercent(value: number | string | null | undefined): string {
  return `${normalize(pctFormatter.format(Number(value ?? 0)))} %`;
}

export function formatDate(value: string | null | undefined): string {
  if (!value) return "—";
  const [datePart] = value.split("T");
  const [y, m, d] = (datePart ?? "").split("-");
  if (!y || !m || !d) return value;
  return `${d}/${m}/${y}`;
}

export function formatDateTime(value: string | null | undefined): string {
  if (!value) return "—";
  const date = new Date(value);
  return normalize(
    date.toLocaleString("fr-FR", { day: "2-digit", month: "2-digit", year: "numeric", hour: "2-digit", minute: "2-digit" }),
  );
}

/** Today as yyyy-MM-dd in local time (for date inputs). */
export function todayIso(): string {
  const d = new Date();
  const pad = (n: number) => String(n).padStart(2, "0");
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`;
}

const MONTHS = ["janv.", "févr.", "mars", "avr.", "mai", "juin", "juil.", "août", "sept.", "oct.", "nov.", "déc."];

/** "2026-03" -> "mars 26" */
export function formatMonth(value: string): string {
  const [y, m] = value.split("-");
  const month = MONTHS[Number(m) - 1] ?? value;
  return `${month} ${y?.slice(2) ?? ""}`;
}
