"use client";

import { usePathname } from "next/navigation";
import { useLayoutEffect, useRef } from "react";
import { Icon } from "@/components/ui/Icon";
import { useAuth } from "@/features/auth/components/AuthProvider";
import { Spring } from "@/lib/motion/spring";
import { SidebarItem } from "../SidebarItem";
import { NAVIGATION } from "./navigation";
import styles from "./Sidebar.module.css";

interface SidebarProps {
  open: boolean;
  onNavigate: () => void;
}

/** Glass navigation rail. A neon indicator glides (spring) to the active link; links enter staggered. */
export function Sidebar({ open, onNavigate }: SidebarProps) {
  const { can } = useAuth();
  const pathname = usePathname();
  const navRef = useRef<HTMLElement>(null);
  const indicatorRef = useRef<HTMLSpanElement>(null);
  const springs = useRef<{ top: Spring; height: Spring } | null>(null);
  const sections = NAVIGATION.map((s) => ({ ...s, links: s.links.filter((l) => !l.permission || can(l.permission)) }))
    .filter((s) => s.links.length > 0);

  useLayoutEffect(() => {
    const el = indicatorRef.current;
    if (!el) return;
    let top = 0;
    let height = 0;
    const apply = () => {
      el.style.transform = `translateY(${top}px)`;
      el.style.height = `${Math.max(0, height)}px`;
    };
    springs.current = {
      top: new Spring(0, (v) => { top = v; apply(); }, { stiffness: 220, damping: 22 }),
      height: new Spring(0, (v) => { height = v; apply(); }, { stiffness: 300, damping: 20 }),
    };
    return () => {
      springs.current?.top.stop();
      springs.current?.height.stop();
    };
  }, []);

  useLayoutEffect(() => {
    const nav = navRef.current;
    const s = springs.current;
    const link = nav?.querySelector<HTMLElement>('[aria-current="page"]');
    const el = indicatorRef.current;
    if (!nav || !s || !el) return;
    if (!link) {
      el.style.opacity = "0";
      return;
    }
    el.style.opacity = "1";
    const top = link.getBoundingClientRect().top - nav.getBoundingClientRect().top + nav.scrollTop;
    if (s.height.current === 0) {
      s.top.jump(top);
      s.height.jump(link.offsetHeight);
    } else {
      // squash while travelling, then settle to the link height
      s.height.set(link.offsetHeight * 0.5);
      s.top.set(top);
      setTimeout(() => s.height.set(link.offsetHeight), 90);
    }
  }, [pathname, sections.length]);

  let linkIndex = 0;
  return (
    <aside className={open ? `${styles.sidebar} ${styles.open}` : styles.sidebar} aria-label="Navigation principale">
      <div className={styles.brand}>
        <span className={styles.logo} aria-hidden>
          <span className={styles.logoCore} />
        </span>
        <span className={styles.name}>
          <span>Gest<strong>Com</strong></span>
          <small>Gestion commerciale</small>
        </span>
      </div>
      <nav className={styles.nav} ref={navRef}>
        <span ref={indicatorRef} className={styles.indicator} aria-hidden />
        {sections.map((section) => (
          <div key={section.label} className={styles.section}>
            <p className={styles.sectionLabel}>
              <Icon name={section.icon} size={14} />
              {section.label}
            </p>
            <ul className={styles.links}>
              {section.links.map((link) => (
                <SidebarItem key={link.href} href={link.href} label={link.label} index={linkIndex++} onNavigate={onNavigate} />
              ))}
            </ul>
          </div>
        ))}
      </nav>
    </aside>
  );
}
