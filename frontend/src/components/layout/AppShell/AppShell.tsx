"use client";

import { useState, type ReactNode } from "react";
import { ScrollProgress } from "@/components/effects/ScrollProgress";
import { Sidebar } from "../Sidebar";
import { Topbar } from "../Topbar";
import styles from "./AppShell.module.css";

export function AppShell({ children }: { children: ReactNode }) {
  const [menuOpen, setMenuOpen] = useState(false);
  return (
    <div className={styles.shell}>
      <ScrollProgress />
      <Sidebar open={menuOpen} onNavigate={() => setMenuOpen(false)} />
      {menuOpen && <div className={styles.backdrop} onClick={() => setMenuOpen(false)} aria-hidden />}
      <div className={styles.main}>
        <Topbar onToggleMenu={() => setMenuOpen((o) => !o)} />
        <main className={styles.content}>{children}</main>
      </div>
    </div>
  );
}
