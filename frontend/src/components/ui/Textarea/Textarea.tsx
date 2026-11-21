import type { ComponentProps } from "react";
import styles from "./Textarea.module.css";

export function Textarea({ className, ...rest }: ComponentProps<"textarea">) {
  return <textarea className={[styles.textarea, className].filter(Boolean).join(" ")} rows={3} {...rest} />;
}
