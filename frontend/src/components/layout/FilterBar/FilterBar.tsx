import type { ReactNode } from "react";
import styles from "./FilterBar.module.css";

/** Horizontal row of filter controls above a list. */
export function FilterBar({ children }: { children: ReactNode }) {
  return <div className={styles.bar}>{children}</div>;
}

export function FilterItem({ grow, children }: { grow?: boolean; children: ReactNode }) {
  return <div className={grow ? `${styles.item} ${styles.grow}` : styles.item}>{children}</div>;
}
