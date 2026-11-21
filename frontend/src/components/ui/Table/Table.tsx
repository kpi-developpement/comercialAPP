"use client";

import type { ReactNode } from "react";
import { EmptyState } from "../EmptyState";
import { ErrorState } from "../ErrorState";
import { Skeleton } from "../Skeleton";
import styles from "./Table.module.css";

export interface Column<T> {
  key: string;
  header: string;
  render: (row: T) => ReactNode;
  /** Backend sort property; makes the header clickable. */
  sortKey?: string;
  align?: "left" | "right" | "center";
  width?: string;
}

interface TableProps<T> {
  columns: Column<T>[];
  rows: T[] | undefined;
  rowKey: (row: T) => string | number;
  loading?: boolean;
  error?: unknown;
  onRetry?: () => void;
  sort?: string;
  onSort?: (field: string) => void;
  onRowClick?: (row: T) => void;
  empty?: ReactNode;
  footer?: ReactNode;
}

/** Data table with sortable headers, loading skeleton, empty and error states. */
export function Table<T>({
  columns,
  rows,
  rowKey,
  loading,
  error,
  onRetry,
  sort,
  onSort,
  onRowClick,
  empty,
  footer,
}: TableProps<T>) {
  const [sortField, sortDir] = (sort ?? "").split(",");

  if (error) {
    return <ErrorState error={error} onRetry={onRetry} />;
  }

  return (
    <div className={styles.wrapper}>
      <table className={styles.table}>
        <thead>
          <tr>
            {columns.map((col) => {
              const active = col.sortKey && col.sortKey === sortField;
              const ariaSort = active ? (sortDir === "desc" ? "descending" : "ascending") : undefined;
              return (
                <th key={col.key} style={{ width: col.width, textAlign: col.align }} aria-sort={ariaSort}>
                  {col.sortKey && onSort ? (
                    <button type="button" className={styles.sortButton} onClick={() => onSort(col.sortKey!)}>
                      {col.header}
                      <span
                        className={[styles.sortIcon, active && styles.sortActive, active && sortDir === "desc" && styles.sortDesc]
                          .filter(Boolean)
                          .join(" ")}
                        aria-hidden
                      >
                        {active ? "▲" : "↕"}
                      </span>
                    </button>
                  ) : (
                    col.header
                  )}
                </th>
              );
            })}
          </tr>
        </thead>
        <tbody>
          {loading &&
            Array.from({ length: 6 }).map((_, i) => (
              <tr key={`sk-${i}`}>
                {columns.map((col) => (
                  <td key={col.key}>
                    <Skeleton />
                  </td>
                ))}
              </tr>
            ))}
          {!loading &&
            rows?.map((row, i) => (
              <tr
                key={rowKey(row)}
                className={onRowClick ? `${styles.row} ${styles.clickable}` : styles.row}
                style={{ "--i": i } as React.CSSProperties}
                onClick={onRowClick ? () => onRowClick(row) : undefined}
                onKeyDown={onRowClick ? (e) => e.key === "Enter" && onRowClick(row) : undefined}
                tabIndex={onRowClick ? 0 : undefined}
              >
                {columns.map((col) => (
                  <td key={col.key} style={{ textAlign: col.align }} className={col.align === "right" ? "num" : undefined}>
                    {col.render(row)}
                  </td>
                ))}
              </tr>
            ))}
        </tbody>
      </table>
      {!loading && rows && rows.length === 0 && (empty ?? <EmptyState title="Aucun résultat" />)}
      {footer}
    </div>
  );
}
