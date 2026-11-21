import type { ListParams } from "@/lib/types";

export const articleKeys = {
  all: ["articles"] as const,
  list: (params: ListParams) => ["articles", "list", params] as const,
  detail: (id: number) => ["articles", "detail", id] as const,
};
