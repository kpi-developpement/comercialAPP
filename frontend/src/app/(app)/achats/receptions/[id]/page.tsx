import { DocumentDetailView } from "@/features/documents/components/DocumentDetailView";

export default async function Page({ params }: { params: Promise<{ id: string }> }) {
  const { id } = await params;
  return <DocumentDetailView type="RECEPTION" id={Number(id)} />;
}
