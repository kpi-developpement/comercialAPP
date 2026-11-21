"use client";

import { useMutation, useQueryClient } from "@tanstack/react-query";
import { documentsApi } from "../api/documents.api";
import type { DocumentDomain } from "../config/documentTypes";
import type { DocumentDetail, DocumentRequest, TransformationRequest, TypeDocument } from "../types/document.types";
import { documentKeys } from "./documentKeys";

/**
 * Every write on a document can change other documents (source statuses, remaining quantities), stock and
 * balances, so lists of all document types plus stock caches are invalidated.
 */
function useInvalidate() {
  const qc = useQueryClient();
  return (doc?: DocumentDetail) => {
    if (doc) qc.setQueryData(documentKeys.detail(doc.type, doc.id), doc);
    qc.invalidateQueries({ queryKey: documentKeys.all, predicate: (q) => !doc || q.queryKey[3] !== doc.id });
    qc.invalidateQueries({ queryKey: ["stock"] });
    qc.invalidateQueries({ queryKey: ["reglements"] });
    qc.invalidateQueries({ queryKey: ["dashboard"] });
  };
}

export function useSaveDocument(type: TypeDocument) {
  const invalidate = useInvalidate();
  return useMutation({
    mutationFn: ({ id, body }: { id?: number; body: DocumentRequest }) =>
      id ? documentsApi.update(type, id, body) : documentsApi.create(type, body),
    onSuccess: invalidate,
  });
}

export function useValiderDocument(type: TypeDocument) {
  const invalidate = useInvalidate();
  return useMutation({ mutationFn: (id: number) => documentsApi.valider(type, id), onSuccess: invalidate });
}

export function useAnnulerDocument(type: TypeDocument) {
  const invalidate = useInvalidate();
  return useMutation({ mutationFn: (id: number) => documentsApi.annuler(type, id), onSuccess: invalidate });
}

export function useDeleteDocument(type: TypeDocument) {
  const invalidate = useInvalidate();
  return useMutation({ mutationFn: (id: number) => documentsApi.remove(type, id), onSuccess: () => invalidate() });
}

export function useTransformDocument(type: TypeDocument) {
  const invalidate = useInvalidate();
  return useMutation({
    mutationFn: ({ id, body }: { id: number; body: TransformationRequest }) => documentsApi.transformer(type, id, body),
    onSuccess: invalidate,
  });
}

export function useGroupDocuments(domain: DocumentDomain) {
  const invalidate = useInvalidate();
  return useMutation({
    mutationFn: (body: { documentIds: number[]; dateDocument: string | null }) => documentsApi.grouper(domain, body),
    onSuccess: invalidate,
  });
}
