"use client";

import { useState } from "react";
import { Card } from "@/components/ui/Card";
import { FormField } from "@/components/ui/FormField";
import { Input } from "@/components/ui/Input";
import { Table } from "@/components/ui/Table";
import { formatMoney } from "@/lib/format";
import { useDebouncedValue } from "@/lib/use-debounced-value";
import { useCaAnnuel } from "../../hooks/useClientStats";
import styles from "./ClientProduitsPanel.module.css";

/** C.A. TTC des années (factures − avoirs), with a bar proportional to the best year. */
export function CaAnnuelCard({ clientId }: { clientId: number }) {
  const current = new Date().getFullYear();
  const [de, setDe] = useState(String(current - 2));
  const [a, setA] = useState(String(current));
  const anneeDe = useDebouncedValue(Number(de), 400);
  const anneeA = useDebouncedValue(Number(a), 400);
  const query = useCaAnnuel(clientId, anneeDe, anneeA);
  const max = Math.max(1, ...(query.data ?? []).map((r) => Number(r.montantTtc)));

  return (
    <Card title="C.A. (T.T.C.) des années" padded={false}>
      <div className={styles.toolbar}>
        <div className={styles.years}>
          <FormField label="Année du" htmlFor="ca-de">
            <Input id="ca-de" type="number" min="2000" max={current} value={de} onChange={(e) => setDe(e.target.value)} />
          </FormField>
        </div>
        <div className={styles.years}>
          <FormField label="Année au" htmlFor="ca-a">
            <Input id="ca-a" type="number" min="2000" max={current + 1} value={a} onChange={(e) => setA(e.target.value)} />
          </FormField>
        </div>
      </div>
      <Table
        columns={[
          { key: "annee", header: "Année", width: "100px", render: (r) => <strong>{r.annee}</strong> },
          {
            key: "montant",
            header: "Montant TTC",
            align: "right",
            render: (r) => (
              <>
                {formatMoney(r.montantTtc)}
                <span className={styles.bar} style={{ width: `${Math.max(0, (Number(r.montantTtc) / max) * 100)}%` }} />
              </>
            ),
          },
        ]}
        rows={query.data}
        rowKey={(r) => r.annee}
        loading={query.isLoading}
        error={query.error}
      />
    </Card>
  );
}
