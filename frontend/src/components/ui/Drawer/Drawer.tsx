"use client";

import { useEffect, type ReactNode } from "react";
import { createPortal } from "react-dom";
import styles from "./Drawer.module.css";

interface DrawerProps {
  open: boolean;
  title: string;
  onClose: () => void;
  children: ReactNode;
}

/** Side panel for secondary details, rendered in a portal and sliding in with a spring. */
export function Drawer({ open, title, onClose, children }: DrawerProps) {
  useEffect(() => {
    if (!open) return;
    const onKey = (e: KeyboardEvent) => e.key === "Escape" && onClose();
    document.addEventListener("keydown", onKey);
    return () => document.removeEventListener("keydown", onKey);
  }, [open, onClose]);

  if (!open || typeof document === "undefined") return null;
  return createPortal(
    <div className={styles.overlay} onMouseDown={(e) => e.target === e.currentTarget && onClose()}>
      <aside className={styles.panel} role="dialog" aria-modal="true" aria-label={title}>
        <header className={styles.header}>
          <h2>{title}</h2>
          <button type="button" className={styles.close} onClick={onClose} aria-label="Fermer">
            ×
          </button>
        </header>
        <div className={styles.body}>{children}</div>
      </aside>
    </div>,
    document.body,
  );
}
