import type { ListParams } from "@/lib/types";
import type { TypeDocument } from "../types/document.types";

export const documentKeys = {
  all: ["documents"] as const,
  type: (type: TypeDocument) => ["documents", type] as const,
  list: (type: TypeDocument, params: ListParams) => ["documents", type, "list", params] as const,
  detail: (type: TypeDocument, id: number) => ["documents", type, "detail", id] as const,
};
