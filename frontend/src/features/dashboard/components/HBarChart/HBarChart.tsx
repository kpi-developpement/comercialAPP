"use client";

import { useRef } from "react";
import { formatMoney } from "@/lib/format";
import { useInView } from "@/lib/motion/useInView";
import type { Point } from "../../types/dashboard.types";
import styles from "./HBarChart.module.css";

interface HBarChartProps {
  data: Point[];
  ariaLabel: string;
  /** CSS variable name of each bar color (cycled), e.g. ["--chart-1"]. */
  colors?: string[];
  emptyLabel?: string;
}

const W = 520;
const ROW = 30;
const LABEL_W = 190;
const VALUE_W = 100;

/** Horizontal bars that sweep in one after the other when scrolled into view. */
export function HBarChart({ data, ariaLabel, colors = ["--chart-1"], emptyLabel = "Aucune donnée sur la période" }: HBarChartProps) {
  const ref = useRef<HTMLDivElement>(null);
  const visible = useInView(ref);
  if (data.length === 0 || data.every((d) => Number(d.valeur) === 0)) {
    return <p className={styles.empty}>{emptyLabel}</p>;
  }
  const max = Math.max(...data.map((d) => Number(d.valeur)), 1);
  const barArea = W - LABEL_W - VALUE_W;
  const H = data.length * ROW + 8;

  return (
    <div ref={ref} className={visible ? `${styles.wrap} ${styles.visible}` : styles.wrap}>
      <svg className={styles.chart} viewBox={`0 0 ${W} ${H}`} role="img" aria-label={ariaLabel}>
        {data.map((d, i) => {
          const v = Math.max(0, Number(d.valeur));
          const w = (v / max) * barArea;
          const y = 4 + i * ROW;
          const label = d.label.length > 28 ? `${d.label.slice(0, 27)}…` : d.label;
          return (
            <g key={`${d.label}-${i}`} className={styles.row} style={{ "--i": i } as React.CSSProperties}>
              <rect x={LABEL_W} y={y + 6} width={barArea} height={ROW - 12} rx={6} className={styles.track} />
              <text x={LABEL_W - 10} y={y + ROW / 2} className={styles.label} textAnchor="end" dominantBaseline="middle">
                {label}
                <title>{d.label}</title>
              </text>
              <rect x={LABEL_W} y={y + 6} width={Math.max(3, w)} height={ROW - 12} rx={6} className={styles.bar}
                style={{ fill: `var(${colors[i % colors.length]})` }}>
                <title>{`${d.label} : ${formatMoney(v)} MAD`}</title>
              </rect>
              <text x={LABEL_W + w + 8} y={y + ROW / 2} className={styles.value} dominantBaseline="middle">{formatMoney(v)}</text>
            </g>
          );
        })}
      </svg>
    </div>
  );
}
