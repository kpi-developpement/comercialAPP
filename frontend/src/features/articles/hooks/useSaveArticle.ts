"use client";

import { useMutation, useQueryClient } from "@tanstack/react-query";
import { articlesApi } from "../api/articles.api";
import type { ArticleRequest } from "../types/article.types";
import { articleKeys } from "./articleKeys";

export function useSaveArticle() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({ id, body }: { id?: number; body: ArticleRequest }) =>
      id ? articlesApi.update(id, body) : articlesApi.create(body),
    onSuccess: (article) => {
      qc.setQueryData(articleKeys.detail(article.id), article);
      qc.invalidateQueries({ queryKey: [...articleKeys.all, "list"] });
    },
  });
}

export function useDeleteArticle() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (id: number) => articlesApi.remove(id),
    onSuccess: () => qc.invalidateQueries({ queryKey: articleKeys.all }),
  });
}
