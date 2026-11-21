import { api, apiBlob } from "@/lib/api-client";
import type { ListParams, PageResponse } from "@/lib/types";
import { DOCUMENT_TYPES, type DocumentDomain } from "../config/documentTypes";
import type {
  DocumentDetail,
  DocumentRequest,
  DocumentSummary,
  TransformationRequest,
  TypeDocument,
} from "../types/document.types";

const base = (type: TypeDocument) => {
  const c = DOCUMENT_TYPES[type];
  return `/api/v1/${c.domain}/${c.path}`;
};

export const documentsApi = {
  list: (type: TypeDocument, params: ListParams) => api.get<PageResponse<DocumentSummary>>(base(type), params),
  get: (type: TypeDocument, id: number) => api.get<DocumentDetail>(`${base(type)}/${id}`),
  create: (type: TypeDocument, body: DocumentRequest) => api.post<DocumentDetail>(base(type), body),
  update: (type: TypeDocument, id: number, body: DocumentRequest) => api.put<DocumentDetail>(`${base(type)}/${id}`, body),
  remove: (type: TypeDocument, id: number) => api.delete(`${base(type)}/${id}`),
  valider: (type: TypeDocument, id: number) => api.post<DocumentDetail>(`${base(type)}/${id}/valider`),
  annuler: (type: TypeDocument, id: number) => api.post<DocumentDetail>(`${base(type)}/${id}/annuler`),
  transformer: (type: TypeDocument, id: number, body: TransformationRequest) =>
    api.post<DocumentDetail>(`${base(type)}/${id}/transformer`, body),
  grouper: (domain: DocumentDomain, body: { documentIds: number[]; dateDocument: string | null }) =>
    api.post<DocumentDetail>(`/api/v1/${domain}/factures/grouper`, body),
  pdf: (type: TypeDocument, id: number) => apiBlob(`${base(type)}/${id}/pdf`),
};
