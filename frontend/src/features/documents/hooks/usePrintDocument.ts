"use client";

import { useState } from "react";
import { useToast } from "@/components/ui/Toast";
import { errorMessage } from "@/lib/api-client";
import { documentsApi } from "../api/documents.api";
import type { TypeDocument } from "../types/document.types";

/** Downloads the PDF with the JWT and opens it in a new tab. */
export function usePrintDocument(type: TypeDocument) {
  const toast = useToast();
  const [printing, setPrinting] = useState(false);
  const print = async (id: number) => {
    setPrinting(true);
    try {
      const blob = await documentsApi.pdf(type, id);
      const url = URL.createObjectURL(blob);
      window.open(url, "_blank", "noopener");
      setTimeout(() => URL.revokeObjectURL(url), 60_000);
    } catch (e) {
      toast.error(errorMessage(e));
    } finally {
      setPrinting(false);
    }
  };
  return { print, printing };
}
