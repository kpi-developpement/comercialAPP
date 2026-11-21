"use client";

import Link from "next/link";
import { Card } from "@/components/ui/Card";
import { Skeleton } from "@/components/ui/Skeleton";
import { routeOf } from "@/features/documents/config/documentTypes";
import { formatDate, formatMoney } from "@/lib/format";
import { useSoldeTiers } from "../../hooks/useReglements";
import type { Sens } from "../../types/reglement.types";
import styles from "./TiersSoldePanel.module.css";

/** Balance of a client / fournisseur and its unpaid invoices. */
export function TiersSoldePanel({ kind, tiersId }: { kind: Sens; tiersId: number }) {
  const { data, isLoading } = useSoldeTiers(kind, tiersId);
  const today = new Date().toISOString().slice(0, 10);

  return (
    <Card title="Solde et impayés">
      {isLoading || !data ? (
        <Skeleton height={140} />
      ) : (
        <>
          <dl className={styles.totals}>
            <div><dt>Facturé</dt><dd>{formatMoney(data.totalFactures)}</dd></div>
            <div><dt>Avoirs</dt><dd>− {formatMoney(data.totalAvoirs)}</dd></div>
            <div><dt>Réglé (affecté)</dt><dd>− {formatMoney(data.totalRegle)}</dd></div>
            <div><dt>Acomptes non affectés</dt><dd>− {formatMoney(data.reglementsNonAffectes)}</dd></div>
            <div className={styles.solde}><dt>Solde</dt><dd>{formatMoney(data.solde, "MAD")}</dd></div>
          </dl>
          <h3 className={styles.subtitle}>Factures non soldées ({data.facturesNonSoldees.length})</h3>
          <ul className={styles.list}>
            {data.facturesNonSoldees.map((f) => (
              <li key={f.id} className={styles.item}>
                <Link href={routeOf(f.type, f.id)}>{f.numero}</Link>
                <span className={f.dateEcheance && f.dateEcheance < today ? styles.late : styles.muted}>
                  Éch. {formatDate(f.dateEcheance)}
                </span>
                <span className="num">{formatMoney(f.resteAPayer)}</span>
              </li>
            ))}
            {data.facturesNonSoldees.length === 0 && <li className={styles.muted}>Aucune facture en attente</li>}
          </ul>
        </>
      )}
    </Card>
  );
}
