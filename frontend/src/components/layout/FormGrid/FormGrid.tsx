import type { ReactNode } from "react";
import styles from "./FormGrid.module.css";

/** Responsive form grid: N columns on desktop, 1 column under tablet width. */
export function FormGrid({ columns = 2, children }: { columns?: 2 | 3 | 4; children: ReactNode }) {
  return <div className={`${styles.grid} ${styles[`cols${columns}`]}`}>{children}</div>;
}

export function FormGridFull({ children }: { children: ReactNode }) {
  return <div className={styles.full}>{children}</div>;
}
