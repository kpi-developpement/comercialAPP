/** "Nice" axis maximum and ticks (1, 2, 2.5, 5 × 10^n). */
export function niceScale(max: number, tickCount = 4): { max: number; ticks: number[] } {
  if (max <= 0) return { max: 1, ticks: [0, 1] };
  const rough = max / tickCount;
  const pow = 10 ** Math.floor(Math.log10(rough));
  const step = [1, 2, 2.5, 5, 10].map((m) => m * pow).find((s) => s >= rough) ?? 10 * pow;
  const niceMax = Math.ceil(max / step) * step;
  const ticks: number[] = [];
  for (let v = 0; v <= niceMax + step / 2; v += step) ticks.push(v);
  return { max: niceMax, ticks };
}

/** Compact amount for axes: 1 250 000 -> "1,3 M", 45 000 -> "45 k". */
export function compactAmount(value: number): string {
  const abs = Math.abs(value);
  if (abs >= 1_000_000) return `${(value / 1_000_000).toLocaleString("fr-FR", { maximumFractionDigits: 1 })} M`;
  if (abs >= 1_000) return `${(value / 1_000).toLocaleString("fr-FR", { maximumFractionDigits: 0 })} k`;
  return value.toLocaleString("fr-FR", { maximumFractionDigits: 0 });
}
