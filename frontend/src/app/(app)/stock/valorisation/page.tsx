import { RequireAuth } from "@/features/auth/components/RequireAuth";
import { ValorisationView } from "@/features/stock/components/ValorisationView";

export default function Page() {
  return (
    <RequireAuth permission="stock.read">
      <ValorisationView />
    </RequireAuth>
  );
}
