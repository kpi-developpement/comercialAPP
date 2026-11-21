"use client";

import { useRouter } from "next/navigation";
import { Card } from "@/components/ui/Card";
import { EmptyState } from "@/components/ui/EmptyState";
import { Table } from "@/components/ui/Table";
import { formatDate, formatMoney } from "@/lib/format";
import { TYPE_REGLEMENT_LABELS } from "@/lib/types";
import { useReglements } from "../../hooks/useReglements";
import type { Sens } from "../../types/reglement.types";
import styles from "./ImpayesHistoryPanel.module.css";

/** Historique des impayés: chèques / effets of the tiers declared unpaid. */
export function ImpayesHistoryPanel({ sens, tiersId }: { sens: Sens; tiersId: number }) {
  const router = useRouter();
  const query = useReglements(sens, { page: 0, size: 100, tiersId, statut: "IMPAYE", sort: "dateReglement,desc" });
  const rows = query.data?.content ?? [];
  const total = rows.reduce((s, r) => s + Number(r.montant), 0);

  return (
    <Card title="Historique des impayés" padded={false}
      actions={rows.length > 0 && <span className={styles.total}>{rows.length} impayé(s) · {formatMoney(total, "MAD")}</span>}>
      <Table
        columns={[
          { key: "numero", header: "Règlement", render: (r) => <strong>{r.numero}</strong> },
          { key: "date", header: "Date", render: (r) => formatDate(r.dateReglement) },
          { key: "mode", header: "Mode", render: (r) => TYPE_REGLEMENT_LABELS[r.mode] },
          { key: "ref", header: "N° titre", render: (r) => r.reference ?? "—" },
          { key: "banque", header: "Banque", render: (r) => r.banque ?? "—" },
          { key: "echeance", header: "Échéance", render: (r) => formatDate(r.dateEcheance) },
          { key: "factures", header: "Factures concernées", render: (r) => r.affectations.map((a) => a.documentNumero).join(", ") || "—" },
          { key: "montant", header: "Montant", align: "right", render: (r) => formatMoney(r.montant) },
        ]}
        rows={rows}
        rowKey={(r) => r.id}
        loading={query.isLoading}
        error={query.error}
        onRetry={() => query.refetch()}
        onRowClick={(r) => router.push(`/reglements/${sens}/${r.id}`)}
        empty={<EmptyState title="Aucun impayé" description="Aucun chèque ni effet impayé pour ce tiers." />}
      />
    </Card>
  );
}
