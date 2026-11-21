"use client";

import { Button } from "@/components/ui/Button";
import { Input } from "@/components/ui/Input";
import type { DocumentSummary } from "@/features/documents/types/document.types";
import { formatDate, formatMoney } from "@/lib/format";
import { round2 } from "@/features/documents/utils/totals";
import styles from "./AllocationTable.module.css";

interface AllocationTableProps {
  factures: DocumentSummary[];
  /** factureId -> amount (as typed). */
  values: Record<number, string>;
  onChange: (values: Record<number, string>) => void;
  /** Amount available to allocate. */
  disponible: number;
}

/** Allocation of a payment to the unpaid invoices of the tiers (oldest due first). */
export function AllocationTable({ factures, values, onChange, disponible }: AllocationTableProps) {
  const total = Object.values(values).reduce((s, v) => s + (Number(v) || 0), 0);
  const overAllocated = round2(total) > round2(disponible);

  const autoAllocate = () => {
    let reste = disponible;
    const next: Record<number, string> = {};
    for (const f of factures) {
      const part = Math.min(reste, Number(f.resteAPayer));
      if (part > 0) next[f.id] = String(round2(part));
      reste = round2(reste - part);
    }
    onChange(next);
  };

  if (factures.length === 0) {
    return <p className={styles.empty}>Aucune facture en attente de règlement pour ce tiers : le montant restera en acompte.</p>;
  }

  return (
    <div>
      <div className={styles.toolbar}>
        <span className={overAllocated ? styles.error : styles.info}>
          Affecté : {formatMoney(total)} / {formatMoney(disponible)}
        </span>
        <Button size="sm" onClick={autoAllocate} disabled={disponible <= 0}>Répartir automatiquement</Button>
      </div>
      <table className={styles.table}>
        <thead>
          <tr>
            <th>Facture</th>
            <th>Date</th>
            <th>Échéance</th>
            <th className="num">Total TTC</th>
            <th className="num">Reste à payer</th>
            <th className="num">Montant affecté</th>
          </tr>
        </thead>
        <tbody>
          {factures.map((f) => {
            const v = Number(values[f.id] ?? 0);
            const bad = v < 0 || v > Number(f.resteAPayer);
            return (
              <tr key={f.id}>
                <td><strong>{f.numero}</strong></td>
                <td>{formatDate(f.dateDocument)}</td>
                <td>{formatDate(f.dateEcheance)}</td>
                <td className="num">{formatMoney(f.totalTtc)}</td>
                <td className="num">{formatMoney(f.resteAPayer)}</td>
                <td className={styles.amount}>
                  <Input type="number" min="0" step="0.01" align="right" invalid={bad} aria-label={`Montant affecté à ${f.numero}`}
                    value={values[f.id] ?? ""} onChange={(e) => onChange({ ...values, [f.id]: e.target.value })} />
                </td>
              </tr>
            );
          })}
        </tbody>
      </table>
      {overAllocated && <p className={styles.error} role="alert">Le total affecté dépasse le montant disponible.</p>}
    </div>
  );
}

export function toAffectations(values: Record<number, string>) {
  return Object.entries(values)
    .map(([id, v]) => ({ documentId: Number(id), montant: round2(Number(v) || 0) }))
    .filter((a) => a.montant > 0);
}
