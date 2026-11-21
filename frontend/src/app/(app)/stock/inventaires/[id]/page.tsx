import { InventaireDetailView } from "@/features/stock/components/InventaireDetailView";

export default async function Page({ params }: { params: Promise<{ id: string }> }) {
  const { id } = await params;
  return <InventaireDetailView id={Number(id)} />;
}
