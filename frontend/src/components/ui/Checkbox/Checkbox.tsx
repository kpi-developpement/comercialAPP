import type { ComponentProps } from "react";
import styles from "./Checkbox.module.css";

interface CheckboxProps extends Omit<ComponentProps<"input">, "type"> {
  label: string;
}

/** Native checkbox (keeps forms working) with a custom box: spring pop + check stroke drawn on toggle. */
export function Checkbox({ label, className, ...rest }: CheckboxProps) {
  return (
    <label className={[styles.wrapper, className].filter(Boolean).join(" ")}>
      <input type="checkbox" className={styles.native} {...rest} />
      <span className={styles.box} aria-hidden>
        <svg viewBox="0 0 16 16" className={styles.tick}>
          <path d="M3.5 8.5l3 3 6-7" />
        </svg>
      </span>
      {label && <span className={styles.label}>{label}</span>}
    </label>
  );
}
