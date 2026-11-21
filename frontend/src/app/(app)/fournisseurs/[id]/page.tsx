import { FournisseurFileView } from "@/features/tiers/components/FournisseurFileView";

export default async function Page({ params }: { params: Promise<{ id: string }> }) {
  const { id } = await params;
  return <FournisseurFileView id={Number(id)} />;
}
