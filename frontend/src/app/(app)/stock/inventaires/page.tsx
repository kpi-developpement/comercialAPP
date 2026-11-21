import { RequireAuth } from "@/features/auth/components/RequireAuth";
import { InventairesListView } from "@/features/stock/components/InventairesListView";

export default function Page() {
  return (
    <RequireAuth permission="stock.read">
      <InventairesListView />
    </RequireAuth>
  );
}
