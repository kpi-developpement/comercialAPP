"use client";

import { Button } from "../Button";
import { Select } from "../Select";
import styles from "./Pagination.module.css";

interface PaginationProps {
  page: number;
  totalPages: number;
  totalElements: number;
  size: number;
  onPageChange: (page: number) => void;
  onSizeChange?: (size: number) => void;
}

const SIZES = [10, 20, 50, 100].map((n) => ({ value: n, label: `${n} / page` }));

export function Pagination({ page, totalPages, totalElements, size, onPageChange, onSizeChange }: PaginationProps) {
  const from = totalElements === 0 ? 0 : page * size + 1;
  const to = Math.min(totalElements, (page + 1) * size);
  return (
    <nav className={styles.pagination} aria-label="Pagination">
      <span className={styles.info}>
        {from}–{to} sur {totalElements}
      </span>
      <div className={styles.controls}>
        {onSizeChange && (
          <Select
            className={styles.size}
            aria-label="Lignes par page"
            options={SIZES}
            value={size}
            onChange={(e) => onSizeChange(Number(e.target.value))}
          />
        )}
        <Button size="sm" variant="ghost" onClick={() => onPageChange(page - 1)} disabled={page <= 0}>
          ‹ Précédent
        </Button>
        <span className={styles.current}>
          Page {totalPages === 0 ? 0 : page + 1} / {totalPages}
        </span>
        <Button size="sm" variant="ghost" onClick={() => onPageChange(page + 1)} disabled={page + 1 >= totalPages}>
          Suivant ›
        </Button>
      </div>
    </nav>
  );
}
