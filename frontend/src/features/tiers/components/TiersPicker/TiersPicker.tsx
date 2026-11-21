"use client";

import { useEffect, useState } from "react";
import { Combobox } from "@/components/ui/Combobox";
import { useDebouncedValue } from "@/lib/use-debounced-value";
import { useTiersList } from "../../hooks/useTiers";
import type { TiersKind, TiersSummary } from "../../types/tiers.types";

interface TiersPickerProps {
  kind: TiersKind;
  /** Label of the current selection. */
  label: string;
  onSelect: (tiers: TiersSummary) => void;
  invalid?: boolean;
  disabled?: boolean;
  id?: string;
}

/** Search a client / fournisseur by code, name, ICE or phone. */
export function TiersPicker({ kind, label, onSelect, invalid, disabled, id }: TiersPickerProps) {
  const [query, setQuery] = useState(label);
  useEffect(() => setQuery(label), [label]);
  const search = useDebouncedValue(query);
  const typing = query !== label;
  const { data, isFetching } = useTiersList(kind, { page: 0, size: 10, search, actif: true, sort: "raisonSociale,asc" }, typing);

  return (
    <Combobox
      id={id}
      ariaLabel={kind === "clients" ? "Client" : "Fournisseur"}
      placeholder="Rechercher par code, nom ou ICE"
      query={query}
      onQueryChange={setQuery}
      loading={typing && isFetching}
      invalid={invalid}
      disabled={disabled}
      items={typing ? (data?.content ?? []).map((t) => ({ key: t.id, value: t, primary: t.raisonSociale, secondary: `${t.code}${t.ville ? " · " + t.ville : ""}` })) : []}
      onSelect={(t) => {
        onSelect(t);
        setQuery(t.raisonSociale);
      }}
    />
  );
}
