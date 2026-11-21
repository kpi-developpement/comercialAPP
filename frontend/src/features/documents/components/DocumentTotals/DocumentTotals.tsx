import { formatMoney, formatQty } from "@/lib/format";
import styles from "./DocumentTotals.module.css";

interface DocumentTotalsProps {
  totalHt: number;
  totalRemise: number;
  totalTva: number;
  totalTtc: number;
  recap: { taux: number; baseHt: number; montantTva: number }[];
  montantEnLettres?: string;
  devise?: string;
}

/** VAT summary per rate + document totals (+ amount in words once computed by the backend). */
export function DocumentTotals({ totalHt, totalRemise, totalTva, totalTtc, recap, montantEnLettres, devise = "MAD" }: DocumentTotalsProps) {
  return (
    <div className={styles.wrapper}>
      <table className={styles.recap}>
        <thead>
          <tr>
            <th>Taux TVA</th>
            <th>Base HT</th>
            <th>Montant TVA</th>
          </tr>
        </thead>
        <tbody>
          {recap.length === 0 && (
            <tr><td colSpan={3} className={styles.muted}>—</td></tr>
          )}
          {recap.map((r) => (
            <tr key={r.taux}>
              <td>{formatQty(r.taux)} %</td>
              <td className="num">{formatMoney(r.baseHt)}</td>
              <td className="num">{formatMoney(r.montantTva)}</td>
            </tr>
          ))}
        </tbody>
      </table>
      <dl className={styles.totals}>
        <div><dt>Total remise</dt><dd>{formatMoney(totalRemise)}</dd></div>
        <div><dt>Total HT</dt><dd>{formatMoney(totalHt)}</dd></div>
        <div><dt>Total TVA</dt><dd>{formatMoney(totalTva)}</dd></div>
        <div className={styles.ttc}><dt>Total TTC</dt><dd>{formatMoney(totalTtc, devise)}</dd></div>
      </dl>
      {montantEnLettres && <p className={styles.words}>Arrêté à la somme de : <strong>{montantEnLettres}</strong></p>}
    </div>
  );
}
