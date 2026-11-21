import type { TiersKind } from "@/features/tiers/types/tiers.types";
import type { Permission } from "@/lib/permissions";
import type { TypeDocument } from "../types/document.types";

export type DocumentDomain = "ventes" | "achats";

/**
 * Everything that differs between document types. Pages only pass the type; shared components read this config.
 */
export interface DocumentTypeConfig {
  type: TypeDocument;
  domain: DocumentDomain;
  /** API and route segment (devis, commandes, livraisons, factures, avoirs, receptions). */
  path: string;
  label: string;
  labelPlural: string;
  nouveau: string;
  tiersKind: TiersKind;
  readPermission: Permission;
  writePermission: Permission;
  isFacture: boolean;
  isAvoir: boolean;
  hasValidite: boolean;
  hasReferenceFournisseur: boolean;
  annulable: boolean;
  /** Type of the documents that can be grouped into this one (factures). */
  groupFrom?: TypeDocument;
}

const vente = (c: Omit<DocumentTypeConfig, "domain" | "tiersKind" | "readPermission" | "hasReferenceFournisseur">): DocumentTypeConfig => ({
  ...c,
  domain: "ventes",
  tiersKind: "clients",
  readPermission: "vente.read",
  hasReferenceFournisseur: false,
});

const achat = (c: Omit<DocumentTypeConfig, "domain" | "tiersKind" | "readPermission" | "hasValidite">): DocumentTypeConfig => ({
  ...c,
  domain: "achats",
  tiersKind: "fournisseurs",
  readPermission: "achat.read",
  hasValidite: false,
});

export const DOCUMENT_TYPES: Record<TypeDocument, DocumentTypeConfig> = {
  DEVIS: vente({ type: "DEVIS", path: "devis", label: "Devis", labelPlural: "Devis", nouveau: "Nouveau devis",
    writePermission: "vente.write", isFacture: false, isAvoir: false, hasValidite: true, annulable: true }),
  BON_COMMANDE: vente({ type: "BON_COMMANDE", path: "commandes", label: "Bon de commande", labelPlural: "Bons de commande",
    nouveau: "Nouveau bon de commande", writePermission: "vente.write", isFacture: false, isAvoir: false, hasValidite: false, annulable: true }),
  BON_LIVRAISON: vente({ type: "BON_LIVRAISON", path: "livraisons", label: "Bon de livraison", labelPlural: "Bons de livraison",
    nouveau: "Nouveau bon de livraison", writePermission: "livraison.write", isFacture: false, isAvoir: false, hasValidite: false, annulable: false }),
  FACTURE: vente({ type: "FACTURE", path: "factures", label: "Facture", labelPlural: "Factures", nouveau: "Nouvelle facture",
    writePermission: "vente.write", isFacture: true, isAvoir: false, hasValidite: false, annulable: false, groupFrom: "BON_LIVRAISON" }),
  AVOIR: vente({ type: "AVOIR", path: "avoirs", label: "Avoir", labelPlural: "Avoirs", nouveau: "Nouvel avoir",
    writePermission: "vente.write", isFacture: false, isAvoir: true, hasValidite: false, annulable: false }),
  BC_FOURNISSEUR: achat({ type: "BC_FOURNISSEUR", path: "commandes", label: "Commande fournisseur", labelPlural: "Commandes fournisseur",
    nouveau: "Nouvelle commande fournisseur", writePermission: "achat.reception", isFacture: false, isAvoir: false,
    hasReferenceFournisseur: false, annulable: true }),
  RECEPTION: achat({ type: "RECEPTION", path: "receptions", label: "Bon de réception", labelPlural: "Réceptions",
    nouveau: "Nouvelle réception", writePermission: "achat.reception", isFacture: false, isAvoir: false,
    hasReferenceFournisseur: true, annulable: false }),
  FACTURE_FOURNISSEUR: achat({ type: "FACTURE_FOURNISSEUR", path: "factures", label: "Facture fournisseur",
    labelPlural: "Factures fournisseur", nouveau: "Nouvelle facture fournisseur", writePermission: "achat.write", isFacture: true,
    isAvoir: false, hasReferenceFournisseur: true, annulable: false, groupFrom: "RECEPTION" }),
  AVOIR_FOURNISSEUR: achat({ type: "AVOIR_FOURNISSEUR", path: "avoirs", label: "Avoir fournisseur", labelPlural: "Avoirs fournisseur",
    nouveau: "Nouvel avoir fournisseur", writePermission: "achat.write", isFacture: false, isAvoir: true,
    hasReferenceFournisseur: true, annulable: false }),
};

export function routeOf(type: TypeDocument, id?: number): string {
  const c = DOCUMENT_TYPES[type];
  return `/${c.domain}/${c.path}${id !== undefined ? `/${id}` : ""}`;
}
