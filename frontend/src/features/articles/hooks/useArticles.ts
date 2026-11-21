"use client";

import { keepPreviousData, useQuery } from "@tanstack/react-query";
import type { ListParams } from "@/lib/types";
import { articlesApi } from "../api/articles.api";
import { articleKeys } from "./articleKeys";

export function useArticles(params: ListParams, enabled = true) {
  return useQuery({
    queryKey: articleKeys.list(params),
    queryFn: () => articlesApi.list(params),
    placeholderData: keepPreviousData,
    enabled,
  });
}
