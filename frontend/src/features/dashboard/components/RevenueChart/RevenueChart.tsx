"use client";

import { useRef } from "react";
import { formatMoney, formatMonth } from "@/lib/format";
import { useInView } from "@/lib/motion/useInView";
import type { Point } from "../../types/dashboard.types";
import { compactAmount, niceScale } from "../../utils/chartScale";
import styles from "./RevenueChart.module.css";

const W = 720;
const H = 270;
const M = { top: 18, right: 12, bottom: 32, left: 56 };

/**
 * Monthly revenue (HT) over 12 months in plain SVG: gradient bars growing in sequence when scrolled into view,
 * plus a smoothed trend line drawn over them.
 */
export function RevenueChart({ data }: { data: Point[] }) {
  const ref = useRef<HTMLDivElement>(null);
  const visible = useInView(ref);
  const values = data.map((d) => Number(d.valeur));
  const { max, ticks } = niceScale(Math.max(0, ...values));
  const innerW = W - M.left - M.right;
  const innerH = H - M.top - M.bottom;
  const band = innerW / Math.max(1, data.length);
  const barW = Math.min(38, band * 0.56);
  const y = (v: number) => M.top + innerH - (Math.max(0, v) / max) * innerH;
  const cx = (i: number) => M.left + i * band + band / 2;
  const path = data.map((d, i) => `${i === 0 ? "M" : "L"}${cx(i).toFixed(1)},${y(Number(d.valeur)).toFixed(1)}`).join(" ");

  return (
    <div ref={ref} className={visible ? `${styles.wrap} ${styles.visible}` : styles.wrap}>
      <svg className={styles.chart} viewBox={`0 0 ${W} ${H}`} role="img" aria-label="Chiffre d'affaires HT par mois">
        <defs>
          <linearGradient id="rev-bar" x1="0" y1="0" x2="0" y2="1">
            <stop offset="0%" stopColor="var(--cyan-400)" />
            <stop offset="100%" stopColor="var(--violet-600)" stopOpacity="0.35" />
          </linearGradient>
          <linearGradient id="rev-line" x1="0" y1="0" x2="1" y2="0">
            <stop offset="0%" stopColor="var(--violet-400)" />
            <stop offset="100%" stopColor="var(--pink-400)" />
          </linearGradient>
        </defs>
        {ticks.map((t) => (
          <g key={t}>
            <line x1={M.left} x2={W - M.right} y1={y(t)} y2={y(t)} className={styles.grid} />
            <text x={M.left - 8} y={y(t)} className={styles.axis} textAnchor="end" dominantBaseline="middle">{compactAmount(t)}</text>
          </g>
        ))}
        {data.map((d, i) => {
          const v = Number(d.valeur);
          const x = cx(i) - barW / 2;
          const top = y(v);
          return (
            <g key={d.label} className={styles.group} style={{ "--i": i } as React.CSSProperties}>
              <rect x={x} y={top} width={barW} height={Math.max(0, M.top + innerH - top)} rx={6} className={styles.bar} fill="url(#rev-bar)">
                <title>{`${formatMonth(d.label)} : ${formatMoney(v)} MAD`}</title>
              </rect>
              <text x={cx(i)} y={H - M.bottom + 18} className={styles.axis} textAnchor="middle">{formatMonth(d.label)}</text>
            </g>
          );
        })}
        {data.length > 1 && <path d={path} className={styles.line} stroke="url(#rev-line)" pathLength={1} />}
        {data.map((d, i) => (
          <circle key={`p-${d.label}`} cx={cx(i)} cy={y(Number(d.valeur))} r={3.5} className={styles.point}
            style={{ "--i": i } as React.CSSProperties} />
        ))}
      </svg>
    </div>
  );
}
