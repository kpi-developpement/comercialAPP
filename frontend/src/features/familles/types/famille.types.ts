export interface Famille {
  id: number;
  code: string;
  libelle: string;
  parentId: number | null;
  parentLibelle: string | null;
}

export interface FamilleRequest {
  code: string;
  libelle: string;
  parentId: number | null;
}
