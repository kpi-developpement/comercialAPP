import { RequireAuth } from "@/features/auth/components/RequireAuth";
import { FournisseurFileView } from "@/features/tiers/components/FournisseurFileView";

export default function Page() {
  return (
    <RequireAuth permission="fournisseur.write">
      <FournisseurFileView />
    </RequireAuth>
  );
}
