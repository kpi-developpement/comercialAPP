import Link from "next/link";
import styles from "./Breadcrumbs.module.css";

export interface Crumb {
  label: string;
  href?: string;
}

export function Breadcrumbs({ items }: { items: Crumb[] }) {
  return (
    <nav aria-label="Fil d'Ariane">
      <ol className={styles.list}>
        {items.map((item, i) => (
          <li key={`${item.label}-${i}`} className={styles.item}>
            {item.href ? <Link href={item.href}>{item.label}</Link> : <span aria-current="page">{item.label}</span>}
          </li>
        ))}
      </ol>
    </nav>
  );
}
