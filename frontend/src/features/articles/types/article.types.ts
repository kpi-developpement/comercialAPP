export type ArticleType = "STOCKE" | "SERVICE";

export const ARTICLE_TYPE_LABELS: Record<ArticleType, string> = {
  STOCKE: "Produit stocké",
  SERVICE: "Service",
};

export interface ArticleSummary {
  id: number;
  code: string;
  designation: string;
  familleLibelle: string | null;
  unite: string;
  prixAchatHt: number;
  prixVenteHt: number;
  tauxTva: number;
  codeBarres: string | null;
  type: ArticleType;
  actif: boolean;
  pump: number;
}

export interface Article extends ArticleSummary {
  familleId: number | null;
  stockMinimum: number;
  createdAt: string;
  updatedAt: string;
}

export interface ArticleRequest {
  code: string;
  designation: string;
  familleId: number | null;
  unite: string;
  prixAchatHt: number;
  prixVenteHt: number;
  tauxTva: number;
  codeBarres: string | null;
  stockMinimum: number;
  actif: boolean;
  type: ArticleType;
}

export interface ArticleFilters extends Record<string, string | number | boolean | undefined> {
  search: string;
  familleId: string;
  type: string;
  actif: string;
}
