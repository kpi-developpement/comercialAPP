"use client";

import { PageHeader } from "@/components/layout/PageHeader";
import { RequireAuth } from "@/features/auth/components/RequireAuth";
import { DOCUMENT_TYPES, routeOf } from "../../config/documentTypes";
import type { TypeDocument } from "../../types/document.types";
import { DocumentEditor } from "../DocumentEditor";

export function DocumentNewView({ type }: { type: TypeDocument }) {
  const config = DOCUMENT_TYPES[type];
  return (
    <RequireAuth permission={config.writePermission}>
      <PageHeader title={config.nouveau} breadcrumbs={[{ label: config.labelPlural, href: routeOf(type) }, { label: "Nouveau" }]} />
      <DocumentEditor type={type} />
    </RequireAuth>
  );
}
