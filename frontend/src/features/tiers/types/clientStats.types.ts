export interface ArticleSuggere {
  id: number;
  articleId: number;
  code: string;
  designation: string;
  prixVenteHt: number;
}

export interface ProduitVendu {
  articleId: number;
  code: string;
  designation: string;
  quantite: number;
  montantHt: number;
  nbFactures: number;
  derniereVente: string | null;
}

export interface ProduitFacture {
  documentId: number;
  type: "FACTURE" | "AVOIR";
  numero: string;
  dateDocument: string;
  prixUnitaireHt: number;
  quantite: number;
  montantHt: number;
}

export interface CaAnnuel {
  annee: number;
  montantTtc: number;
}
