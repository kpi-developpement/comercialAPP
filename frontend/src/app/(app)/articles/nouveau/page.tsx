import { PageHeader } from "@/components/layout/PageHeader";
import { ArticleForm } from "@/features/articles/components/ArticleForm";
import { RequireAuth } from "@/features/auth/components/RequireAuth";

export default function NouvelArticlePage() {
  return (
    <RequireAuth permission="article.write">
      <PageHeader title="Nouvel article" breadcrumbs={[{ label: "Articles", href: "/articles" }, { label: "Nouveau" }]} />
      <ArticleForm />
    </RequireAuth>
  );
}
