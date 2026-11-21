import { Suspense } from "react";
import { PageHeader } from "@/components/layout/PageHeader";
import { RequireAuth } from "@/features/auth/components/RequireAuth";
import { ReglementForm } from "@/features/reglements/components/ReglementForm";

export default function Page() {
  return (
    <RequireAuth permission="reglement.write">
      <PageHeader title="Nouveau règlement" breadcrumbs={[{ label: "Règlements", href: "/reglements/fournisseurs" }, { label: "Nouveau" }]} />
      <Suspense>
        <ReglementForm sens="fournisseurs" />
      </Suspense>
    </RequireAuth>
  );
}
