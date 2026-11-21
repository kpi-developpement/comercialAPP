import Link from "next/link";
import { formatDate, formatMoney } from "@/lib/format";
import type { DocumentTypeConfig } from "../../config/documentTypes";
import type { DocumentDetail } from "../../types/document.types";
import { PaymentStatusBadge } from "../DocumentStatusBadge";
import styles from "./DocumentInfo.module.css";

/** Read-only header of a validated document. */
export function DocumentInfo({ doc, config }: { doc: DocumentDetail; config: DocumentTypeConfig }) {
  return (
    <dl className={styles.grid}>
      <div>
        <dt>{config.tiersKind === "clients" ? "Client" : "Fournisseur"}</dt>
        <dd>
          <Link href={`/${config.tiersKind}/${doc.tiersId}`}>{doc.tiersNom}</Link>
          <span className={styles.sub}>{doc.tiersAdresse}</span>
          {doc.tiersIce && <span className={styles.sub}>ICE {doc.tiersIce}</span>}
        </dd>
      </div>
      <div><dt>Date</dt><dd>{formatDate(doc.dateDocument)}</dd></div>
      <div><dt>Dépôt</dt><dd>{doc.depotLibelle}</dd></div>
      {doc.dateValidite && <div><dt>Valable jusqu&apos;au</dt><dd>{formatDate(doc.dateValidite)}</dd></div>}
      {doc.referenceFournisseur && <div><dt>Réf. fournisseur</dt><dd>{doc.referenceFournisseur}</dd></div>}
      {doc.sources && <div><dt>Origine</dt><dd>{doc.sources}</dd></div>}
      {config.isAvoir && <div><dt>Marchandise</dt><dd>{doc.retourMarchandise ? "Retour en stock" : "Sans retour"}</dd></div>}
      {config.isFacture && (
        <>
          <div><dt>Échéance</dt><dd>{formatDate(doc.dateEcheance)}</dd></div>
          <div><dt>Réglé</dt><dd className="num">{formatMoney(doc.montantRegle)}</dd></div>
          <div>
            <dt>Reste à payer</dt>
            <dd className={styles.reste}>{formatMoney(doc.resteAPayer)} <PaymentStatusBadge statut={doc.statutPaiement} /></dd>
          </div>
        </>
      )}
      <div><dt>Créé par</dt><dd>{doc.createdBy ?? "—"}</dd></div>
      {doc.notes && <div className={styles.full}><dt>Notes</dt><dd>{doc.notes}</dd></div>}
    </dl>
  );
}
