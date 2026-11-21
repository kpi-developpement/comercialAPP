import type { Point } from "../../types/dashboard.types";
import { HBarChart } from "../HBarChart";

const LABELS: Record<string, string> = {
  "0-30": "0 à 30 jours",
  "31-60": "31 à 60 jours",
  "61-90": "61 à 90 jours",
  "90+": "Plus de 90 jours",
};

/** Unpaid invoices by age past due date; color darkens with age. */
export function AgingChart({ data }: { data: Point[] }) {
  return (
    <HBarChart
      data={data.map((d) => ({ ...d, label: LABELS[d.label] ?? d.label }))}
      ariaLabel="Impayés par ancienneté"
      colors={["--chart-2", "--chart-3", "--chart-4", "--color-danger"]}
      emptyLabel="Aucun impayé"
    />
  );
}
