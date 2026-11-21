"use client";

import Link from "next/link";
import { Card } from "@/components/ui/Card";
import { Skeleton } from "@/components/ui/Skeleton";
import type { Article } from "@/features/articles/types/article.types";
import { formatMoney, formatQty } from "@/lib/format";
import { useStockArticle } from "../../hooks/useStockReports";
import styles from "./ArticleStockPanel.module.css";

/** Quantity per dépôt, total, PUMP and value of one article. */
export function ArticleStockPanel({ article }: { article: Article }) {
  const { data, isLoading } = useStockArticle(article.id);
  const total = (data ?? []).reduce((sum, d) => sum + Number(d.quantite), 0);
  const sousMinimum = Number(article.stockMinimum) > 0 && total < Number(article.stockMinimum);

  return (
    <Card title="Stock par dépôt">
      {isLoading ? (
        <Skeleton height={120} />
      ) : (
        <>
          <ul className={styles.list}>
            {(data ?? []).map((d) => (
              <li key={d.depotId} className={styles.row}>
                <span>{d.depotLibelle}</span>
                <span className="num">{formatQty(d.quantite)} {article.unite}</span>
              </li>
            ))}
          </ul>
          <dl className={styles.totals}>
            <div className={sousMinimum ? styles.alert : undefined}>
              <dt>Stock total</dt>
              <dd>{formatQty(total)} {article.unite}</dd>
            </div>
            <div><dt>PUMP</dt><dd>{formatMoney(article.pump)}</dd></div>
            <div><dt>Valeur</dt><dd>{formatMoney(total * Number(article.pump))}</dd></div>
          </dl>
          {sousMinimum && <p className={styles.warning}>Sous le stock minimum ({formatQty(article.stockMinimum)})</p>}
          <Link href={`/stock/mouvements?articleId=${article.id}`} className={styles.link}>Voir les mouvements</Link>
        </>
      )}
    </Card>
  );
}
