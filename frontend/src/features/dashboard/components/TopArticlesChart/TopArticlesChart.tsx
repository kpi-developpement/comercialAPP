import type { Point } from "../../types/dashboard.types";
import { HBarChart } from "../HBarChart";

export function TopArticlesChart({ data }: { data: Point[] }) {
  return <HBarChart data={data} ariaLabel="Top 10 articles par chiffre d'affaires HT" colors={["--chart-2"]} />;
}
