"use client";

import { useQuery } from "@tanstack/react-query";
import { articlesApi } from "../api/articles.api";
import { articleKeys } from "./articleKeys";

export function useArticle(id: number) {
  return useQuery({ queryKey: articleKeys.detail(id), queryFn: () => articlesApi.get(id) });
}
