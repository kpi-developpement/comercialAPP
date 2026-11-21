import { ArticleDetailView } from "@/features/articles/components/ArticleDetailView";

export default async function ArticlePage({ params }: { params: Promise<{ id: string }> }) {
  const { id } = await params;
  return <ArticleDetailView id={Number(id)} />;
}
