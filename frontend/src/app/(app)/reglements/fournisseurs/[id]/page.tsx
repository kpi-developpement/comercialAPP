import { ReglementDetailView } from "@/features/reglements/components/ReglementDetailView";

export default async function Page({ params }: { params: Promise<{ id: string }> }) {
  const { id } = await params;
  return <ReglementDetailView sens="fournisseurs" id={Number(id)} />;
}
