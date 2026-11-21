"use client";

import { useRouter } from "next/navigation";
import { useEffect, useState } from "react";
import { Icon } from "@/components/ui/Icon";
import { useAuth } from "@/features/auth/components/AuthProvider";
import { ROLE_LABELS } from "@/lib/permissions";
import styles from "./Topbar.module.css";

function useClock(): string {
  const [now, setNow] = useState("");
  useEffect(() => {
    const tick = () =>
      setNow(new Date().toLocaleString("fr-FR", { weekday: "short", day: "2-digit", month: "short", hour: "2-digit", minute: "2-digit" }));
    tick();
    const id = setInterval(tick, 15_000);
    return () => clearInterval(id);
  }, []);
  return now;
}

export function Topbar({ onToggleMenu }: { onToggleMenu: () => void }) {
  const { user, logout } = useAuth();
  const router = useRouter();
  const clock = useClock();

  const handleLogout = () => {
    logout();
    router.replace("/login");
  };

  const initials = user?.nomComplet.split(" ").map((p) => p[0]).slice(0, 2).join("").toUpperCase();

  return (
    <header className={styles.topbar}>
      <button type="button" className={styles.menu} onClick={onToggleMenu} aria-label="Ouvrir le menu">
        <Icon name="menu" />
      </button>
      <div className={styles.status}>
        <span className={styles.pulse} aria-hidden />
        <span>Système en ligne</span>
        {clock && <span className={styles.clock}>{clock}</span>}
      </div>
      <div className={styles.spacer} />
      {user && (
        <div className={styles.user}>
          <span className={styles.avatar} aria-hidden>{initials}</span>
          <div className={styles.identity}>
            <span className={styles.name}>{user.nomComplet}</span>
            <span className={styles.role}>{ROLE_LABELS[user.role]}</span>
          </div>
          <button type="button" className={styles.logout} onClick={handleLogout} title="Se déconnecter">
            <Icon name="logout" />
            <span className="visually-hidden">Se déconnecter</span>
          </button>
        </div>
      )}
    </header>
  );
}
