"use client";

import { useRouter } from "next/navigation";
import { useEffect, useMemo, useState } from "react";
import { PageHeader } from "@/components/layout/PageHeader";
import { Button } from "@/components/ui/Button";
import { Card } from "@/components/ui/Card";
import { ConfirmDialog } from "@/components/ui/ConfirmDialog";
import { ErrorState } from "@/components/ui/ErrorState";
import { Input } from "@/components/ui/Input";
import { Skeleton } from "@/components/ui/Skeleton";
import { useToast } from "@/components/ui/Toast";
import { useAuth } from "@/features/auth/components/AuthProvider";
import { errorMessage } from "@/lib/api-client";
import { formatDate, formatQty } from "@/lib/format";
import { useDeleteInventaire, useInventaire, useSaisirComptage, useValiderInventaire } from "../../hooks/useInventaires";
import { OperationStatusBadge } from "../OperationStatusBadge";
import styles from "./InventaireDetailView.module.css";

/** Counting sheet: type counted quantities, see gaps live, then validate to generate adjustments. */
export function InventaireDetailView({ id }: { id: number }) {
  const router = useRouter();
  const toast = useToast();
  const { can } = useAuth();
  const query = useInventaire(id);
  const saisir = useSaisirComptage(id);
  const valider = useValiderInventaire(id);
  const remove = useDeleteInventaire();
  const [comptes, setComptes] = useState<Record<number, string>>({});
  const [filtre, setFiltre] = useState("");
  const [confirm, setConfirm] = useState<"valider" | "supprimer" | null>(null);

  useEffect(() => {
    if (query.data) {
      setComptes(Object.fromEntries(query.data.lignes.map((l) => [l.id, l.quantiteComptee === null ? "" : String(Number(l.quantiteComptee))])));
    }
  }, [query.data]);

  const lignes = useMemo(() => {
    const f = filtre.trim().toLowerCase();
    return (query.data?.lignes ?? []).filter((l) => !f || l.articleCode.toLowerCase().includes(f) || l.designation.toLowerCase().includes(f));
  }, [query.data, filtre]);

  if (query.isLoading) return <Skeleton height={300} />;
  if (query.error || !query.data) return <ErrorState error={query.error} onRetry={() => query.refetch()} />;
  const inv = query.data;
  const editable = inv.statut === "BROUILLON" && can("stock.write");
  const nbComptes = Object.values(comptes).filter((v) => v !== "").length;
  const nbEcarts = inv.lignes.filter((l) => comptes[l.id] !== "" && comptes[l.id] !== undefined
    && Number(comptes[l.id]) !== Number(l.quantiteTheorique)).length;

  const payload = () => ({
    notes: inv.notes,
    lignes: inv.lignes.map((l) => ({ ligneId: l.id, quantiteComptee: comptes[l.id] === "" || comptes[l.id] === undefined ? null : Number(comptes[l.id]) })),
  });

  const onSave = async () => {
    try {
      await saisir.mutateAsync(payload());
      toast.success("Comptage enregistré");
    } catch (e) {
      toast.error(errorMessage(e));
    }
  };

  const onConfirm = async () => {
    try {
      if (confirm === "valider") {
        await saisir.mutateAsync(payload());
        const res = await valider.mutateAsync();
        toast.success(`Inventaire ${res.numero} validé : ajustements de stock générés`);
      } else if (confirm === "supprimer") {
        await remove.mutateAsync(inv.id);
        toast.success("Inventaire supprimé");
        router.replace("/stock/inventaires");
      }
    } catch (e) {
      toast.error(errorMessage(e));
    } finally {
      setConfirm(null);
    }
  };

  return (
    <>
      <PageHeader
        title={inv.numero ? `Inventaire ${inv.numero}` : "Inventaire (brouillon)"}
        breadcrumbs={[{ label: "Inventaires", href: "/stock/inventaires" }, { label: inv.numero ?? "Brouillon" }]}
        subtitle={<><OperationStatusBadge statut={inv.statut} /> {inv.depotLibelle} · {formatDate(inv.dateInventaire)} · {nbComptes}/{inv.lignes.length} comptés · {nbEcarts} écart(s)</>}
        actions={editable && (
          <>
            <Button variant="ghost" onClick={() => setConfirm("supprimer")}>Supprimer</Button>
            <Button onClick={onSave} loading={saisir.isPending}>Enregistrer le comptage</Button>
            <Button variant="primary" onClick={() => setConfirm("valider")} disabled={nbComptes === 0}>Valider l&apos;inventaire</Button>
          </>
        )}
      />
      <Card padded={false}>
        <div className={styles.toolbar}>
          <Input type="search" aria-label="Filtrer" placeholder="Filtrer par code ou désignation" value={filtre} onChange={(e) => setFiltre(e.target.value)} />
        </div>
        <div className={styles.scroll}>
          <table className={styles.table}>
            <thead>
              <tr>
                <th>Code</th>
                <th>Désignation</th>
                <th className="num">Théorique</th>
                <th className="num">Compté</th>
                <th className="num">Écart</th>
              </tr>
            </thead>
            <tbody>
              {lignes.map((l) => {
                const v = comptes[l.id] ?? "";
                const ecart = v === "" ? null : Number(v) - Number(l.quantiteTheorique);
                return (
                  <tr key={l.id}>
                    <td><strong>{l.articleCode}</strong></td>
                    <td>{l.designation}</td>
                    <td className="num">{formatQty(l.quantiteTheorique)}</td>
                    <td className={styles.input}>
                      {editable ? (
                        <Input type="number" min="0" step="0.001" align="right" aria-label={`Quantité comptée ${l.articleCode}`}
                          value={v} onChange={(e) => setComptes((c) => ({ ...c, [l.id]: e.target.value }))} />
                      ) : (
                        <span className="num">{l.quantiteComptee === null ? "—" : formatQty(l.quantiteComptee)}</span>
                      )}
                    </td>
                    <td className={`num ${ecart === null || ecart === 0 ? "" : ecart > 0 ? styles.plus : styles.minus}`}>
                      {ecart === null ? "—" : `${ecart > 0 ? "+" : ""}${formatQty(ecart)}`}
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      </Card>
      <ConfirmDialog
        open={!!confirm}
        title={confirm === "valider" ? "Valider l'inventaire" : "Supprimer l'inventaire"}
        message={confirm === "valider"
          ? `${nbEcarts} écart(s) vont générer des mouvements d'ajustement. Les articles non comptés restent inchangés.`
          : "La feuille de comptage sera supprimée."}
        confirmLabel={confirm === "valider" ? "Valider et ajuster le stock" : "Supprimer l'inventaire"}
        danger={confirm === "supprimer"}
        loading={valider.isPending || remove.isPending}
        onConfirm={onConfirm}
        onCancel={() => setConfirm(null)}
      />
    </>
  );
}
