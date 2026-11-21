import styles from "./SituationFiscaleBadge.module.css";

/** "Situation fiscale : régulière / irrégulière" banner of a supplier. */
export function SituationFiscaleBadge({ reguliere, large = false }: { reguliere: boolean; large?: boolean }) {
  return (
    <span className={[styles.badge, reguliere ? styles.ok : styles.ko, large && styles.large].filter(Boolean).join(" ")}>
      Situation fiscale : {reguliere ? "régulière" : "irrégulière"}
    </span>
  );
}
