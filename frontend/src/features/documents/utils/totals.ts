/**
 * Live totals for the editor (UX only: the backend recomputes and is the source of truth).
 * Same algorithm as DocumentCalculator: HALF_UP at 2 decimals per line, then summed.
 */
export interface LineAmounts {
  brut: number;
  remise: number;
  ht: number;
  tva: number;
  ttc: number;
}

export interface Totals {
  totalHt: number;
  totalRemise: number;
  totalTva: number;
  totalTtc: number;
  recap: { taux: number; baseHt: number; montantTva: number }[];
}

interface LineInput {
  quantite: number;
  prixUnitaireHt: number;
  remisePct: number;
  tauxTva: number;
}

/** Round half up to 2 decimals, robust to binary floating point (1.005 -> 1.01). */
export function round2(value: number): number {
  const sign = value < 0 ? -1 : 1;
  return (sign * Math.round(Math.abs(value) * 100 + 1e-9)) / 100;
}

const n = (v: number) => (Number.isFinite(v) ? v : 0);

export function computeLine(line: LineInput): LineAmounts {
  const brut = round2(n(line.quantite) * n(line.prixUnitaireHt));
  const remise = round2((brut * n(line.remisePct)) / 100);
  const ht = round2(brut - remise);
  const tva = round2((ht * n(line.tauxTva)) / 100);
  return { brut, remise, ht, tva, ttc: round2(ht + tva) };
}

export function computeTotals(lines: LineInput[]): Totals {
  const byRate = new Map<number, { baseHt: number; montantTva: number }>();
  let totalHt = 0;
  let totalRemise = 0;
  let totalTva = 0;
  for (const line of lines) {
    const a = computeLine(line);
    totalHt += a.ht;
    totalRemise += a.remise;
    totalTva += a.tva;
    const rate = n(line.tauxTva);
    const acc = byRate.get(rate) ?? { baseHt: 0, montantTva: 0 };
    acc.baseHt += a.ht;
    acc.montantTva += a.tva;
    byRate.set(rate, acc);
  }
  const recap = [...byRate.entries()]
    .sort(([a], [b]) => b - a)
    .map(([taux, v]) => ({ taux, baseHt: round2(v.baseHt), montantTva: round2(v.montantTva) }));
  return {
    totalHt: round2(totalHt),
    totalRemise: round2(totalRemise),
    totalTva: round2(totalTva),
    totalTtc: round2(totalHt + totalTva),
    recap,
  };
}
