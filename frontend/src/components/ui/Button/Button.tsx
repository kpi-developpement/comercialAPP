"use client";

import { useRef, useState, type ComponentProps, type PointerEvent, type ReactNode } from "react";
import { useMagnetic } from "@/lib/motion/useMagnetic";
import { Spinner } from "../Spinner";
import styles from "./Button.module.css";

export type ButtonVariant = "primary" | "secondary" | "ghost" | "danger";

interface ButtonProps extends ComponentProps<"button"> {
  variant?: ButtonVariant;
  size?: "sm" | "md";
  loading?: boolean;
  icon?: ReactNode;
}

interface Ripple {
  id: number;
  x: number;
  y: number;
}

let rippleId = 0;

/** Magnetic button with click ripple, light sweep and a press spring. */
export function Button({
  variant = "secondary",
  size = "md",
  loading = false,
  icon,
  className,
  children,
  disabled,
  type = "button",
  onPointerDown,
  ...rest
}: ButtonProps) {
  const ref = useRef<HTMLButtonElement>(null);
  const [ripples, setRipples] = useState<Ripple[]>([]);
  useMagnetic(ref, size === "sm" ? 3 : 5, !disabled && !loading);

  const onDown = (e: PointerEvent<HTMLButtonElement>) => {
    const r = e.currentTarget.getBoundingClientRect();
    const ripple = { id: rippleId++, x: e.clientX - r.left, y: e.clientY - r.top };
    setRipples((all) => [...all, ripple]);
    setTimeout(() => setRipples((all) => all.filter((x) => x.id !== ripple.id)), 650);
    onPointerDown?.(e);
  };

  const classes = [styles.button, styles[variant], styles[size], loading && styles.loading, className]
    .filter(Boolean)
    .join(" ");
  return (
    <button
      ref={ref}
      type={type}
      className={classes}
      disabled={disabled || loading}
      aria-busy={loading}
      onPointerDown={onDown}
      {...rest}
    >
      <span className={styles.content}>
        {loading ? <Spinner size="sm" /> : icon}
        {children && <span>{children}</span>}
      </span>
      {ripples.map((r) => (
        <span key={r.id} className={styles.ripple} style={{ left: r.x, top: r.y }} aria-hidden />
      ))}
    </button>
  );
}
