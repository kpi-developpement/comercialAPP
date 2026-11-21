"use client";

import { useRef, type ReactNode } from "react";
import { useParallax } from "@/lib/motion/useParallax";
import { Breadcrumbs, type Crumb } from "../Breadcrumbs";
import styles from "./PageHeader.module.css";

interface PageHeaderProps {
  title: string;
  subtitle?: ReactNode;
  breadcrumbs?: Crumb[];
  actions?: ReactNode;
}

/** Page title with gradient ink, letter-by-letter entrance and a slight scroll parallax. */
export function PageHeader({ title, subtitle, breadcrumbs, actions }: PageHeaderProps) {
  const ref = useRef<HTMLDivElement>(null);
  useParallax(ref, 0.12);
  const words = title.split(" ");
  return (
    <div className={styles.header} ref={ref}>
      {breadcrumbs && <Breadcrumbs items={breadcrumbs} />}
      <div className={styles.row}>
        <div className={styles.titles}>
          <h1 className={styles.title} aria-label={title}>
            {words.map((w, i) => (
              <span key={`${w}-${i}`} className={styles.word} style={{ "--i": i } as React.CSSProperties} aria-hidden>
                {w}
              </span>
            ))}
          </h1>
          {subtitle && <div className={styles.subtitle}>{subtitle}</div>}
        </div>
        {actions && <div className={styles.actions}>{actions}</div>}
      </div>
    </div>
  );
}
