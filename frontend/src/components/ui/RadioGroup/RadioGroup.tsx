import type { ChangeEventHandler, FocusEventHandler, Ref } from "react";
import type { Option } from "@/lib/types";
import styles from "./RadioGroup.module.css";

interface RadioGroupProps {
  legend: string;
  options: Option<string>[];
  /** Spread of react-hook-form's register(...) (name, onChange, onBlur, ref). */
  name: string;
  onChange?: ChangeEventHandler<HTMLInputElement>;
  onBlur?: FocusEventHandler<HTMLInputElement>;
  ref?: Ref<HTMLInputElement>;
  disabled?: boolean;
  direction?: "row" | "column";
}

/** Group of native radio buttons with a custom animated dot, framed by a legend. */
export function RadioGroup({ legend, options, name, onChange, onBlur, ref, disabled, direction = "column" }: RadioGroupProps) {
  return (
    <fieldset className={styles.group} disabled={disabled}>
      <legend className={styles.legend}>{legend}</legend>
      <div className={direction === "row" ? `${styles.options} ${styles.row}` : styles.options}>
        {options.map((o) => (
          <label key={o.value} className={styles.option}>
            <input type="radio" className={styles.native} name={name} value={o.value} onChange={onChange} onBlur={onBlur} ref={ref} />
            <span className={styles.dot} aria-hidden />
            <span>{o.label}</span>
          </label>
        ))}
      </div>
    </fieldset>
  );
}
