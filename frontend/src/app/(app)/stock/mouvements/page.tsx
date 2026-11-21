import { Suspense } from "react";
import { RequireAuth } from "@/features/auth/components/RequireAuth";
import { MouvementsView } from "@/features/stock/components/MouvementsView";

export default function Page() {
  return (
    <RequireAuth permission="stock.read">
      <Suspense>
        <MouvementsView />
      </Suspense>
    </RequireAuth>
  );
}
