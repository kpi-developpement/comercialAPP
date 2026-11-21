import { ClientFileView } from "@/features/tiers/components/ClientFileView";

export default async function Page({ params }: { params: Promise<{ id: string }> }) {
  const { id } = await params;
  return <ClientFileView id={Number(id)} />;
}
