import type { IconName } from "@/components/ui/Icon";
import type { Permission } from "@/lib/permissions";

export interface NavLink {
  label: string;
  href: string;
  permission?: Permission;
}

export interface NavSection {
  label: string;
  icon: IconName;
  links: NavLink[];
}

/** Sidebar menu. Links whose permission the user lacks are hidden; empty sections too. */
export const NAVIGATION: NavSection[] = [
  { label: "Tableau de bord", icon: "dashboard", links: [{ label: "Tableau de bord", href: "/dashboard", permission: "dashboard" }] },
  {
    label: "Articles",
    icon: "box",
    links: [
      { label: "Articles", href: "/articles" },
      { label: "Familles", href: "/familles" },
    ],
  },
  {
    label: "Tiers",
    icon: "users",
    links: [
      { label: "Clients", href: "/clients" },
      { label: "Fournisseurs", href: "/fournisseurs" },
    ],
  },
  {
    label: "Ventes",
    icon: "file",
    links: [
      { label: "Devis", href: "/ventes/devis", permission: "vente.read" },
      { label: "Bons de commande", href: "/ventes/commandes", permission: "vente.read" },
      { label: "Bons de livraison", href: "/ventes/livraisons", permission: "vente.read" },
      { label: "Factures", href: "/ventes/factures", permission: "vente.read" },
      { label: "Avoirs", href: "/ventes/avoirs", permission: "vente.read" },
    ],
  },
  {
    label: "Achats",
    icon: "cart",
    links: [
      { label: "Commandes fournisseur", href: "/achats/commandes", permission: "achat.read" },
      { label: "Réceptions", href: "/achats/receptions", permission: "achat.read" },
      { label: "Factures fournisseur", href: "/achats/factures", permission: "achat.read" },
      { label: "Avoirs fournisseur", href: "/achats/avoirs", permission: "achat.read" },
    ],
  },
  {
    label: "Stock",
    icon: "layers",
    links: [
      { label: "État du stock", href: "/stock/etat", permission: "stock.read" },
      { label: "Alertes", href: "/stock/alertes", permission: "stock.read" },
      { label: "Valorisation", href: "/stock/valorisation", permission: "stock.read" },
      { label: "Mouvements", href: "/stock/mouvements", permission: "stock.read" },
      { label: "Inventaires", href: "/stock/inventaires", permission: "stock.read" },
      { label: "Transferts", href: "/stock/transferts", permission: "stock.read" },
      { label: "Dépôts", href: "/stock/depots", permission: "stock.read" },
    ],
  },
  {
    label: "Règlements",
    icon: "wallet",
    links: [
      { label: "Règlements clients", href: "/reglements/clients", permission: "reglement.read" },
      { label: "Règlements fournisseurs", href: "/reglements/fournisseurs", permission: "reglement.read" },
      { label: "Échéancier", href: "/reglements/echeancier", permission: "reglement.read" },
    ],
  },
  {
    label: "Administration",
    icon: "settings",
    links: [
      { label: "Utilisateurs", href: "/utilisateurs", permission: "admin" },
      { label: "Paramètres", href: "/parametres", permission: "admin" },
    ],
  },
];
