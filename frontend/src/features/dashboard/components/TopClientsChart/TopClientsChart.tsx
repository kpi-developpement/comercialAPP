import type { Point } from "../../types/dashboard.types";
import { HBarChart } from "../HBarChart";

export function TopClientsChart({ data }: { data: Point[] }) {
  return <HBarChart data={data} ariaLabel="Top 10 clients par chiffre d'affaires HT" />;
}
