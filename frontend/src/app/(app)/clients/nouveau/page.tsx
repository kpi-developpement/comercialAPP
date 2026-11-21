import { RequireAuth } from "@/features/auth/components/RequireAuth";
import { ClientFileView } from "@/features/tiers/components/ClientFileView";

export default function Page() {
  return (
    <RequireAuth permission="client.write">
      <ClientFileView />
    </RequireAuth>
  );
}
