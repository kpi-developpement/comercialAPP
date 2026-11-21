import { Table, type Column } from "@/components/ui/Table";
import { formatMoney, formatQty } from "@/lib/format";
import type { DocumentLigne } from "../../types/document.types";

/** Lines of a validated document, with the quantity still to transform. */
export function DocumentLinesReadonly({ lignes, showRestante }: { lignes: DocumentLigne[]; showRestante: boolean }) {
  const columns: Column<DocumentLigne>[] = [
    { key: "code", header: "Code", width: "110px", render: (l) => l.articleCode ?? "—" },
    { key: "designation", header: "Désignation", render: (l) => l.designation },
    { key: "qte", header: "Qté", align: "right", render: (l) => `${formatQty(l.quantite)} ${l.unite ?? ""}` },
    { key: "pu", header: "PU HT", align: "right", render: (l) => formatMoney(l.prixUnitaireHt) },
    { key: "remise", header: "Remise", align: "right", render: (l) => `${formatQty(l.remisePct)} %` },
    { key: "tva", header: "TVA", align: "right", render: (l) => `${formatQty(l.tauxTva)} %` },
    { key: "ht", header: "Montant HT", align: "right", render: (l) => formatMoney(l.montantHt) },
  ];
  if (showRestante) {
    columns.push({ key: "reste", header: "Reste à transformer", align: "right", render: (l) => formatQty(l.quantiteRestante) });
  }
  return <Table columns={columns} rows={lignes} rowKey={(l) => l.id} />;
}
