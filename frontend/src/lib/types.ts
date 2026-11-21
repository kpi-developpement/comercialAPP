/** Shapes shared by every feature. */

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export interface ListParams {
  page: number;
  size: number;
  sort?: string;
  [filter: string]: string | number | boolean | undefined;
}

export interface Option<V extends string | number = string> {
  value: V;
  label: string;
}

export type TypeReglement = "ESPECES" | "CHEQUE" | "EFFET" | "VIREMENT" | "CARTE";

export const TYPE_REGLEMENT_LABELS: Record<TypeReglement, string> = {
  ESPECES: "Espèces",
  CHEQUE: "Chèque",
  EFFET: "Effet (LCN)",
  VIREMENT: "Virement",
  CARTE: "Carte bancaire",
};
