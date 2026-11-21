import { RequireAuth } from "@/features/auth/components/RequireAuth";
import { EtatStockView } from "@/features/stock/components/EtatStockView";

export default function Page() {
  return (
    <RequireAuth permission="stock.read">
      <EtatStockView />
    </RequireAuth>
  );
}
