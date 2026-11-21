"use client";

import { useRef, type ElementType, type ReactNode } from "react";
import { useInView } from "@/lib/motion/useInView";
import styles from "./Reveal.module.css";

interface RevealProps {
  children: ReactNode;
  /** Position in a staggered group: delays the entrance by index × step. */
  index?: number;
  effect?: "rise" | "zoom" | "blur" | "slide";
  as?: ElementType;
  className?: string;
}

/** Scroll-triggered entrance (once), with optional stagger index. */
export function Reveal({ children, index = 0, effect = "rise", as: Tag = "div", className }: RevealProps) {
  const ref = useRef<HTMLDivElement>(null);
  const visible = useInView(ref);
  return (
    <Tag
      ref={ref}
      className={[styles.reveal, styles[effect], visible && styles.visible, className].filter(Boolean).join(" ")}
      style={{ "--i": index } as React.CSSProperties}
    >
      {children}
    </Tag>
  );
}
