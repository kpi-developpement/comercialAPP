"use client";

import { Button } from "@/components/ui/Button";
import { Card } from "@/components/ui/Card";
import { EmptyState } from "@/components/ui/EmptyState";
import { Icon } from "@/components/ui/Icon";
import { Table } from "@/components/ui/Table";
import { useToast } from "@/components/ui/Toast";
import { ArticlePicker } from "@/features/documents/components/ArticlePicker";
import { errorMessage } from "@/lib/api-client";
import { formatMoney } from "@/lib/format";
import { useAddSuggestion, useRemoveSuggestion, useSuggestions } from "../../hooks/useClientStats";
import styles from "./ClientProduitsPanel.module.css";

/** Catalogue des produits suggérés à ce client. */
export function SuggestionsCard({ clientId, canWrite }: { clientId: number; canWrite: boolean }) {
  const toast = useToast();
  const query = useSuggestions(clientId);
  const add = useAddSuggestion(clientId);
  const remove = useRemoveSuggestion(clientId);

  const onAdd = async (articleId: number) => {
    try {
      await add.mutateAsync(articleId);
      toast.success("Produit ajouté au catalogue du client");
    } catch (e) {
      toast.error(errorMessage(e));
    }
  };

  return (
    <Card title="Catalogue des produits suggérés" padded={false}>
      {canWrite && (
        <div className={styles.toolbar}>
          <div className={styles.grow}>
            <ArticlePicker label="" ariaLabel="Ajouter un produit suggéré" onSelect={(a) => onAdd(a.id)} />
          </div>
        </div>
      )}
      <Table
        columns={[
          { key: "code", header: "Code produit", width: "120px", render: (s) => <strong>{s.code}</strong> },
          { key: "designation", header: "Désignation", render: (s) => s.designation },
          { key: "prix", header: "Prix HT", align: "right", render: (s) => formatMoney(s.prixVenteHt) },
          {
            key: "actions",
            header: "",
            align: "right",
            render: (s) => canWrite && (
              <Button size="sm" variant="ghost" aria-label={`Retirer ${s.code}`} onClick={() => remove.mutate(s.id)}>
                <Icon name="trash" size={14} />
              </Button>
            ),
          },
        ]}
        rows={query.data}
        rowKey={(s) => s.id}
        loading={query.isLoading}
        error={query.error}
        empty={<EmptyState title="Aucun produit suggéré" description="Ajoutez les articles à proposer à ce client." />}
      />
    </Card>
  );
}
