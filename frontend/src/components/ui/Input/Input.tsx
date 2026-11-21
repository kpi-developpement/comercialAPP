import type { ComponentProps } from "react";
import styles from "./Input.module.css";

interface InputProps extends ComponentProps<"input"> {
  invalid?: boolean;
  align?: "left" | "right";
}

export function Input({ invalid, align = "left", className, ...rest }: InputProps) {
  const classes = [styles.input, align === "right" && styles.right, invalid && styles.invalid, className]
    .filter(Boolean)
    .join(" ");
  return <input className={classes} aria-invalid={invalid || undefined} {...rest} />;
}
