"use client";

import Link from "next/link";
import { Card } from "@/components/ui/Card";
import { Table } from "@/components/ui/Table";
import { routeOf } from "@/features/documents/config/documentTypes";
import { formatDate, formatMoney, formatQty } from "@/lib/format";
import { useFacturesProduit } from "../../hooks/useClientStats";
import styles from "./ClientProduitsPanel.module.css";

interface Props {
  clientId: number;
  article: { id: number; label: string } | null;
}

/** Quantité et prix unitaire de vente du produit sélectionné, facture par facture. */
export function FacturesProduitCard({ clientId, article }: Props) {
  const query = useFacturesProduit(clientId, article?.id ?? null);
  return (
    <Card title={article ? `Qté et prix U. par facture · ${article.label}` : "Qté et prix U. de vente par produit / facture"} padded={false}>
      {!article ? (
        <p className={styles.hint}>Sélectionnez un produit dans la liste des produits vendus.</p>
      ) : (
        <Table
          columns={[
            {
              key: "numero",
              header: "Facture",
              render: (f) => <Link href={routeOf(f.type === "AVOIR" ? "AVOIR" : "FACTURE", f.documentId)}>{f.numero}</Link>,
            },
            { key: "date", header: "Date", render: (f) => formatDate(f.dateDocument) },
            { key: "pu", header: "Prix U. HT", align: "right", render: (f) => formatMoney(f.prixUnitaireHt) },
            {
              key: "qte",
              header: "Qté",
              align: "right",
              render: (f) => <span className={Number(f.quantite) < 0 ? styles.negative : undefined}>{formatQty(f.quantite)}</span>,
            },
            { key: "montant", header: "Montant HT", align: "right", render: (f) => formatMoney(f.montantHt) },
          ]}
          rows={query.data}
          rowKey={(f) => `${f.documentId}-${f.numero}-${f.quantite}`}
          loading={query.isLoading}
          error={query.error}
        />
      )}
    </Card>
  );
}
