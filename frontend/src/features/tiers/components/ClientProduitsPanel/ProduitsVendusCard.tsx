"use client";

import { Card } from "@/components/ui/Card";
import { EmptyState } from "@/components/ui/EmptyState";
import { Table } from "@/components/ui/Table";
import { formatDate, formatMoney, formatQty } from "@/lib/format";
import { useProduitsVendus } from "../../hooks/useClientStats";

interface Props {
  clientId: number;
  selectedId: number | null;
  onSelect: (article: { id: number; label: string }) => void;
}

/** Liste des produits vendus (factures − avoirs), the selected row drives the invoice detail card. */
export function ProduitsVendusCard({ clientId, selectedId, onSelect }: Props) {
  const query = useProduitsVendus(clientId);
  return (
    <Card title="Liste des produits vendus" padded={false}>
      <Table
        columns={[
          {
            key: "code",
            header: "Code produit",
            width: "120px",
            render: (p) => <strong>{selectedId === p.articleId ? `▸ ${p.code}` : p.code}</strong>,
          },
          { key: "designation", header: "Désignation", render: (p) => p.designation },
          { key: "qte", header: "Qté", align: "right", render: (p) => formatQty(p.quantite) },
          { key: "ht", header: "Montant HT", align: "right", render: (p) => formatMoney(p.montantHt) },
          { key: "date", header: "Dernière vente", render: (p) => formatDate(p.derniereVente) },
        ]}
        rows={query.data}
        rowKey={(p) => p.articleId}
        loading={query.isLoading}
        error={query.error}
        onRowClick={(p) => onSelect({ id: p.articleId, label: `${p.code} – ${p.designation}` })}
        empty={<EmptyState title="Aucun produit vendu à ce client" />}
      />
    </Card>
  );
}
