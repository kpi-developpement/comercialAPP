"use client";

import { FilterBar, FilterItem } from "@/components/layout/FilterBar";
import { Input } from "@/components/ui/Input";
import { Select } from "@/components/ui/Select";
import type { DocumentTypeConfig } from "../../config/documentTypes";
import type { DocumentFilters } from "../../types/document.types";
import { PAIEMENT_OPTIONS, STATUT_OPTIONS } from "../DocumentStatusBadge";

interface Props {
  config: DocumentTypeConfig;
  filters: DocumentFilters;
  onChange: <K extends keyof DocumentFilters>(key: K, value: DocumentFilters[K]) => void;
}

export function DocumentsFilters({ config, filters, onChange }: Props) {
  return (
    <FilterBar>
      <FilterItem grow>
        <Input
          type="search"
          aria-label="Rechercher"
          placeholder={`Numéro, ${config.tiersKind === "clients" ? "client" : "fournisseur"} ou document d'origine`}
          value={filters.search}
          onChange={(e) => onChange("search", e.target.value)}
        />
      </FilterItem>
      <FilterItem>
        <Select aria-label="Statut" placeholder="Tous les statuts" options={STATUT_OPTIONS} value={filters.statut}
          onChange={(e) => onChange("statut", e.target.value)} />
      </FilterItem>
      {config.isFacture && (
        <FilterItem>
          <Select aria-label="Paiement" placeholder="Tous les paiements" options={PAIEMENT_OPTIONS} value={filters.statutPaiement}
            onChange={(e) => onChange("statutPaiement", e.target.value)} />
        </FilterItem>
      )}
      <FilterItem>
        <Input type="date" aria-label="Du" value={filters.dateDebut} onChange={(e) => onChange("dateDebut", e.target.value)} />
      </FilterItem>
      <FilterItem>
        <Input type="date" aria-label="Au" value={filters.dateFin} onChange={(e) => onChange("dateFin", e.target.value)} />
      </FilterItem>
    </FilterBar>
  );
}
