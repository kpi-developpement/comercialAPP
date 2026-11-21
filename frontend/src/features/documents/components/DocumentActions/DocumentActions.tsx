"use client";

import Link from "next/link";
import { useRouter } from "next/navigation";
import { useState } from "react";
import { Button } from "@/components/ui/Button";
import { ConfirmDialog } from "@/components/ui/ConfirmDialog";
import { Icon } from "@/components/ui/Icon";
import { useToast } from "@/components/ui/Toast";
import { useAuth } from "@/features/auth/components/AuthProvider";
import { errorMessage } from "@/lib/api-client";
import { DOCUMENT_TYPES, routeOf } from "../../config/documentTypes";
import {
  useAnnulerDocument,
  useDeleteDocument,
  useTransformDocument,
  useValiderDocument,
} from "../../hooks/useDocumentMutations";
import { usePrintDocument } from "../../hooks/usePrintDocument";
import type { DocumentDetail, TransformationRequest } from "../../types/document.types";
import { TransformDialog } from "../TransformDialog";

type Pending = "valider" | "annuler" | "supprimer" | null;

/** Valider, transformer, imprimer, annuler, supprimer — shown according to status and role. */
export function DocumentActions({ doc }: { doc: DocumentDetail }) {
  const config = DOCUMENT_TYPES[doc.type];
  const { can } = useAuth();
  const router = useRouter();
  const toast = useToast();
  const valider = useValiderDocument(doc.type);
  const annuler = useAnnulerDocument(doc.type);
  const supprimer = useDeleteDocument(doc.type);
  const transformer = useTransformDocument(doc.type);
  const { print, printing } = usePrintDocument(doc.type);
  const [pending, setPending] = useState<Pending>(null);
  const [transforming, setTransforming] = useState(false);

  const canWrite = can(config.writePermission);
  const brouillon = doc.statut === "BROUILLON";

  const run = async () => {
    try {
      if (pending === "valider") {
        const res = await valider.mutateAsync(doc.id);
        toast.success(`${config.label} ${res.numero} validé`);
        res.warnings.forEach((w) => toast.info(w));
      } else if (pending === "annuler") {
        await annuler.mutateAsync(doc.id);
        toast.success("Document annulé");
      } else if (pending === "supprimer") {
        await supprimer.mutateAsync(doc.id);
        toast.success("Brouillon supprimé");
        router.replace(routeOf(doc.type));
      }
    } catch (e) {
      toast.error(errorMessage(e));
    } finally {
      setPending(null);
    }
  };

  const onTransform = async (request: TransformationRequest) => {
    try {
      const created = await transformer.mutateAsync({ id: doc.id, body: request });
      toast.success(`${DOCUMENT_TYPES[created.type].label} brouillon créé`);
      setTransforming(false);
      router.push(routeOf(created.type, created.id));
    } catch (e) {
      toast.error(errorMessage(e));
    }
  };

  const confirmTexts: Record<Exclude<Pending, null>, { title: string; message: string; label: string; danger?: boolean }> = {
    valider: {
      title: `Valider ${config.label.toLowerCase()}`,
      message: "Un numéro définitif va être attribué et les mouvements de stock générés. Le document ne sera plus modifiable.",
      label: "Valider le document",
    },
    annuler: {
      title: "Annuler le document",
      message: "Le document sera marqué annulé et ne pourra plus être transformé.",
      label: "Annuler le document",
      danger: true,
    },
    supprimer: {
      title: "Supprimer le brouillon",
      message: "Le brouillon sera supprimé définitivement. Les quantités reprises d'un document source lui sont restituées.",
      label: "Supprimer le brouillon",
      danger: true,
    },
  };
  const confirm = pending ? confirmTexts[pending] : null;

  return (
    <>
      <Button icon={<Icon name="printer" size={16} />} onClick={() => print(doc.id)} loading={printing}>Imprimer</Button>
      {canWrite && brouillon && (
        <>
          <Button variant="ghost" onClick={() => setPending("supprimer")}>Supprimer</Button>
          <Button variant="primary" icon={<Icon name="check" size={16} />} onClick={() => setPending("valider")}>Valider</Button>
        </>
      )}
      {canWrite && config.annulable && doc.statut === "VALIDE" && (
        <Button variant="ghost" onClick={() => setPending("annuler")}>Annuler</Button>
      )}
      {config.isFacture && doc.statut !== "BROUILLON" && Number(doc.resteAPayer) > 0 && can("reglement.write") && (
        <Link href={`/reglements/${config.tiersKind}/nouveau?tiersId=${doc.tiersId}&factureId=${doc.id}`}>
          <Button>Enregistrer un règlement</Button>
        </Link>
      )}
      {doc.transformationsPossibles.length > 0 && (can("vente.write") || can("achat.reception") || can("livraison.write")) && (
        <Button variant="primary" icon={<Icon name="arrow" size={16} />} onClick={() => setTransforming(true)}>Transformer</Button>
      )}
      {confirm && (
        <ConfirmDialog
          open
          title={confirm.title}
          message={confirm.message}
          confirmLabel={confirm.label}
          danger={confirm.danger}
          loading={valider.isPending || annuler.isPending || supprimer.isPending}
          onConfirm={run}
          onCancel={() => setPending(null)}
        />
      )}
      <TransformDialog open={transforming} doc={doc} loading={transformer.isPending}
        onClose={() => setTransforming(false)} onConfirm={onTransform} />
    </>
  );
}
