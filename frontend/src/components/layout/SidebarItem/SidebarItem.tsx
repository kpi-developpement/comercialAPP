"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import styles from "./SidebarItem.module.css";

interface SidebarItemProps {
  href: string;
  label: string;
  /** Global position, used to stagger the entrance. */
  index: number;
  onNavigate: () => void;
}

export function SidebarItem({ href, label, index, onNavigate }: SidebarItemProps) {
  const pathname = usePathname();
  const active = pathname === href || pathname.startsWith(`${href}/`);
  return (
    <li className={styles.li} style={{ "--i": index } as React.CSSProperties}>
      <Link
        href={href}
        className={active ? `${styles.item} ${styles.active}` : styles.item}
        aria-current={active ? "page" : undefined}
        onClick={onNavigate}
      >
        <span className={styles.dot} aria-hidden />
        {label}
      </Link>
    </li>
  );
}
