import type { ReactNode } from "react";
import styles from "./PageTransition.module.css";

/** Route entrance: blur-in + rise with a spring settle. Re-mounted on every navigation by app/(app)/template.tsx. */
export function PageTransition({ children }: { children: ReactNode }) {
  return <div className={styles.page}>{children}</div>;
}
