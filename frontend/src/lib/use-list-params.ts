"use client";

import { useCallback, useState } from "react";
import type { ListParams } from "./types";

export interface ListState<F> {
  page: number;
  size: number;
  sort: string | undefined;
  filters: F;
  setPage: (page: number) => void;
  setSize: (size: number) => void;
  toggleSort: (field: string) => void;
  setFilters: (filters: F) => void;
  updateFilter: <K extends keyof F>(key: K, value: F[K]) => void;
  params: ListParams;
}

/**
 * Pagination + sort + filters state of a list page. Changing a filter goes back to page 0.
 */
export function useListParams<F extends Record<string, string | number | boolean | undefined>>(
  initialFilters: F,
  initialSort?: string,
  initialSize = 20,
): ListState<F> {
  const [page, setPage] = useState(0);
  const [size, setSizeState] = useState(initialSize);
  const [sort, setSort] = useState<string | undefined>(initialSort);
  const [filters, setFiltersState] = useState<F>(initialFilters);

  const toggleSort = useCallback((field: string) => {
    setSort((current) => {
      if (current === `${field},asc`) return `${field},desc`;
      return `${field},asc`;
    });
    setPage(0);
  }, []);

  const setFilters = useCallback((next: F) => {
    setFiltersState(next);
    setPage(0);
  }, []);

  const updateFilter = useCallback(<K extends keyof F>(key: K, value: F[K]) => {
    setFiltersState((current) => ({ ...current, [key]: value }));
    setPage(0);
  }, []);

  const setSize = useCallback((next: number) => {
    setSizeState(next);
    setPage(0);
  }, []);

  return {
    page,
    size,
    sort,
    filters,
    setPage,
    setSize,
    toggleSort,
    setFilters,
    updateFilter,
    params: { page, size, sort, ...filters },
  };
}
