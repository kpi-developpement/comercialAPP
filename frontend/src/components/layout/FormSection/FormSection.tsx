import type { ReactNode } from "react";
import styles from "./FormSection.module.css";

/** Titled block inside a form tab (e.g. "Renseignements fiscaux et juridiques"). */
export function FormSection({ title, children }: { title: string; children: ReactNode }) {
  return (
    <section className={styles.section}>
      <h3 className={styles.title}>{title}</h3>
      {children}
    </section>
  );
}
