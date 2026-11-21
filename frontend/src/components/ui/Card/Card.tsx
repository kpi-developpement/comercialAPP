"use client";

import { useRef, type ReactNode } from "react";
import { Reveal } from "@/components/motion/Reveal";
import { useSpotlight } from "@/lib/motion/useSpotlight";
import styles from "./Card.module.css";

interface CardProps {
  title?: string;
  actions?: ReactNode;
  padded?: boolean;
  className?: string;
  /** Stagger position when several cards enter together. */
  index?: number;
  children: ReactNode;
}

/** Glass panel with a gradient edge, a light that follows the cursor and a scroll-triggered entrance. */
export function Card({ title, actions, padded = true, className, index = 0, children }: CardProps) {
  const ref = useRef<HTMLElement>(null);
  useSpotlight(ref);
  return (
    <Reveal index={index} className={className}>
      <section ref={ref} className={styles.card}>
        {(title || actions) && (
          <header className={styles.header}>
            {title && <h2 className={styles.title}>{title}</h2>}
            {actions && <div className={styles.actions}>{actions}</div>}
          </header>
        )}
        <div className={padded ? styles.body : styles.flush}>{children}</div>
      </section>
    </Reveal>
  );
}
