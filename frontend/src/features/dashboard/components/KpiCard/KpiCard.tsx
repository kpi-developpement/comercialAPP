"use client";

import { useRef } from "react";
import { Reveal } from "@/components/motion/Reveal";
import { useCountUp } from "@/lib/motion/useCountUp";
import { useTilt } from "@/lib/motion/useTilt";
import styles from "./KpiCard.module.css";

interface KpiCardProps {
  label: string;
  value: number;
  format: (value: number) => string;
  hint?: string;
  tone?: "default" | "danger" | "success";
  index?: number;
}

/** KPI tile: 3D spring tilt with glare, value counting up, staggered scroll entrance. */
export function KpiCard({ label, value, format, hint, tone = "default", index = 0 }: KpiCardProps) {
  const ref = useRef<HTMLDivElement>(null);
  useTilt(ref, 7);
  const animated = useCountUp(value);
  return (
    <Reveal index={index} effect="zoom">
      <div ref={ref} className={`${styles.card} ${styles[tone]}`}>
        <span className={styles.glare} aria-hidden />
        <p className={styles.label}>{label}</p>
        <p className={styles.value} aria-label={format(value)}>{format(animated)}</p>
        {hint && <p className={styles.hint}>{hint}</p>}
        <span className={styles.spark} aria-hidden />
      </div>
    </Reveal>
  );
}
