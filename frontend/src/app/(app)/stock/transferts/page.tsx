import { RequireAuth } from "@/features/auth/components/RequireAuth";
import { TransfertsListView } from "@/features/stock/components/TransfertsListView";

export default function Page() {
  return (
    <RequireAuth permission="stock.read">
      <TransfertsListView />
    </RequireAuth>
  );
}
