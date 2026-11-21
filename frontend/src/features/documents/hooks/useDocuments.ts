"use client";

import { keepPreviousData, useQuery } from "@tanstack/react-query";
import type { ListParams } from "@/lib/types";
import { documentsApi } from "../api/documents.api";
import type { TypeDocument } from "../types/document.types";
import { documentKeys } from "./documentKeys";

export function useDocuments(type: TypeDocument, params: ListParams, enabled = true) {
  return useQuery({
    queryKey: documentKeys.list(type, params),
    queryFn: () => documentsApi.list(type, params),
    placeholderData: keepPreviousData,
    enabled,
  });
}

export function useDocument(type: TypeDocument, id: number) {
  return useQuery({ queryKey: documentKeys.detail(type, id), queryFn: () => documentsApi.get(type, id) });
}
