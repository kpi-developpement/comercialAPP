import { RequireAuth } from "@/features/auth/components/RequireAuth";
import { DepotsView } from "@/features/stock/components/DepotsView";

export default function Page() {
  return (
    <RequireAuth permission="stock.read">
      <DepotsView />
    </RequireAuth>
  );
}
