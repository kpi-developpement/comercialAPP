"use client";

import { createContext, useCallback, useContext, useMemo, useState, type ReactNode } from "react";
import styles from "./Toast.module.css";

type ToastTone = "success" | "error" | "info";

interface ToastItem {
  id: number;
  tone: ToastTone;
  message: string;
}

interface ToastApi {
  success: (message: string) => void;
  error: (message: string) => void;
  info: (message: string) => void;
}

const ToastContext = createContext<ToastApi | null>(null);

let nextId = 1;

export function ToastProvider({ children }: { children: ReactNode }) {
  const [items, setItems] = useState<ToastItem[]>([]);

  const dismiss = useCallback((id: number) => setItems((all) => all.filter((t) => t.id !== id)), []);

  const push = useCallback(
    (tone: ToastTone, message: string) => {
      const id = nextId++;
      setItems((all) => [...all.slice(-3), { id, tone, message }]);
      setTimeout(() => dismiss(id), tone === "error" ? 7000 : 4000);
    },
    [dismiss],
  );

  const api = useMemo<ToastApi>(
    () => ({
      success: (m) => push("success", m),
      error: (m) => push("error", m),
      info: (m) => push("info", m),
    }),
    [push],
  );

  return (
    <ToastContext.Provider value={api}>
      {children}
      <div className={styles.region} aria-live="polite">
        {items.map((t) => (
          <div
            key={t.id}
            className={`${styles.toast} ${styles[t.tone]}`}
            role={t.tone === "error" ? "alert" : "status"}
            style={{ "--toast-ms": `${t.tone === "error" ? 7000 : 4000}ms` } as React.CSSProperties}
          >
            <span className={styles.message}>{t.message}</span>
            <button type="button" className={styles.close} onClick={() => dismiss(t.id)} aria-label="Fermer">
              ×
            </button>
          </div>
        ))}
      </div>
    </ToastContext.Provider>
  );
}

export function useToast(): ToastApi {
  const ctx = useContext(ToastContext);
  if (!ctx) throw new Error("useToast doit être utilisé dans ToastProvider");
  return ctx;
}
