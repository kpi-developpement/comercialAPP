"use client";

import { useState } from "react";
import { CaAnnuelCard } from "./CaAnnuelCard";
import { FacturesProduitCard } from "./FacturesProduitCard";
import { ProduitsVendusCard } from "./ProduitsVendusCard";
import { SuggestionsCard } from "./SuggestionsCard";
import styles from "./ClientProduitsPanel.module.css";

/**
 * "Produits vendus par client": suggested catalogue, products sold, price/quantity history of the selected
 * product per invoice, and yearly revenue.
 */
export function ClientProduitsPanel({ clientId, canWrite }: { clientId: number; canWrite: boolean }) {
  const [selected, setSelected] = useState<{ id: number; label: string } | null>(null);
  return (
    <div className={styles.grid}>
      <SuggestionsCard clientId={clientId} canWrite={canWrite} />
      <FacturesProduitCard clientId={clientId} article={selected} />
      <ProduitsVendusCard clientId={clientId} selectedId={selected?.id ?? null} onSelect={setSelected} />
      <CaAnnuelCard clientId={clientId} />
    </div>
  );
}
