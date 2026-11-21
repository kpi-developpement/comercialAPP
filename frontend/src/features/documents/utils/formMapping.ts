import { todayIso } from "@/lib/format";
import type { DocumentFormValues } from "../schemas/document.schema";
import type { DocumentDetail, DocumentRequest } from "../types/document.types";

export function toFormValues(doc: DocumentDetail | undefined, defaultDepotId: number | undefined): DocumentFormValues {
  if (!doc) {
    return {
      tiersId: null,
      tiersNom: "",
      dateDocument: todayIso(),
      depotId: defaultDepotId ?? null,
      notes: "",
      dateEcheance: "",
      dateValidite: "",
      retourMarchandise: false,
      referenceFournisseur: "",
      lignes: [],
    };
  }
  return {
    tiersId: doc.tiersId,
    tiersNom: doc.tiersNom,
    dateDocument: doc.dateDocument,
    depotId: doc.depotId,
    notes: doc.notes ?? "",
    dateEcheance: doc.dateEcheance ?? "",
    dateValidite: doc.dateValidite ?? "",
    retourMarchandise: doc.retourMarchandise,
    referenceFournisseur: doc.referenceFournisseur ?? "",
    lignes: doc.lignes.map((l) => ({
      articleId: l.articleId,
      articleCode: l.articleCode ?? "",
      designation: l.designation,
      unite: l.unite ?? "",
      quantite: Number(l.quantite),
      prixUnitaireHt: Number(l.prixUnitaireHt),
      remisePct: Number(l.remisePct),
      tauxTva: Number(l.tauxTva),
      sourceLigneId: l.sourceLigneId,
      articleStocke: l.articleStocke,
    })),
  };
}

const blank = (s: string) => (s.trim() === "" ? null : s.trim());

export function toRequest(values: DocumentFormValues): DocumentRequest {
  return {
    tiersId: values.tiersId!,
    dateDocument: values.dateDocument,
    depotId: values.depotId!,
    notes: blank(values.notes),
    dateEcheance: blank(values.dateEcheance),
    dateValidite: blank(values.dateValidite),
    retourMarchandise: values.retourMarchandise,
    referenceFournisseur: blank(values.referenceFournisseur),
    lignes: values.lignes.map((l) => ({
      articleId: l.articleId,
      designation: l.designation,
      quantite: l.quantite,
      prixUnitaireHt: l.prixUnitaireHt,
      remisePct: l.remisePct,
      tauxTva: l.tauxTva,
      sourceLigneId: l.sourceLigneId,
    })),
  };
}
