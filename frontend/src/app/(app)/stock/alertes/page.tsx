import { RequireAuth } from "@/features/auth/components/RequireAuth";
import { AlertesStockView } from "@/features/stock/components/AlertesStockView";

export default function Page() {
  return (
    <RequireAuth permission="stock.read">
      <AlertesStockView />
    </RequireAuth>
  );
}
