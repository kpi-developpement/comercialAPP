"use client";

import { FilterBar, FilterItem } from "@/components/layout/FilterBar";
import { Input } from "@/components/ui/Input";
import { Select } from "@/components/ui/Select";
import { useFamilleOptions } from "@/features/familles/hooks/useFamilles";
import type { ArticleFilters } from "../../types/article.types";

interface ArticlesFiltersProps {
  filters: ArticleFilters;
  onChange: <K extends keyof ArticleFilters>(key: K, value: ArticleFilters[K]) => void;
}

export function ArticlesFilters({ filters, onChange }: ArticlesFiltersProps) {
  const { data: familles = [] } = useFamilleOptions();
  return (
    <FilterBar>
      <FilterItem grow>
        <Input
          type="search"
          aria-label="Rechercher un article"
          placeholder="Code, désignation ou code-barres"
          value={filters.search}
          onChange={(e) => onChange("search", e.target.value)}
        />
      </FilterItem>
      <FilterItem>
        <Select
          aria-label="Famille"
          options={familles}
          placeholder="Toutes les familles"
          value={filters.familleId}
          onChange={(e) => onChange("familleId", e.target.value)}
        />
      </FilterItem>
      <FilterItem>
        <Select
          aria-label="Type"
          placeholder="Tous les types"
          options={[
            { value: "STOCKE", label: "Produits stockés" },
            { value: "SERVICE", label: "Services" },
          ]}
          value={filters.type}
          onChange={(e) => onChange("type", e.target.value)}
        />
      </FilterItem>
      <FilterItem>
        <Select
          aria-label="Statut"
          placeholder="Actifs et inactifs"
          options={[
            { value: "true", label: "Actifs" },
            { value: "false", label: "Inactifs" },
          ]}
          value={filters.actif}
          onChange={(e) => onChange("actif", e.target.value)}
        />
      </FilterItem>
    </FilterBar>
  );
}
