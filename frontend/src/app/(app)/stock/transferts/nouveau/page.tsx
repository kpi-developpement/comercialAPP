import { PageHeader } from "@/components/layout/PageHeader";
import { RequireAuth } from "@/features/auth/components/RequireAuth";
import { TransfertForm } from "@/features/stock/components/TransfertForm";

export default function Page() {
  return (
    <RequireAuth permission="stock.write">
      <PageHeader title="Nouveau transfert" breadcrumbs={[{ label: "Transferts", href: "/stock/transferts" }, { label: "Nouveau" }]} />
      <TransfertForm />
    </RequireAuth>
  );
}
