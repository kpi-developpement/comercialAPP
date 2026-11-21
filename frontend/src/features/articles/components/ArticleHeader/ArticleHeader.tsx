"use client";

import { useRouter } from "next/navigation";
import { useState } from "react";
import { PageHeader } from "@/components/layout/PageHeader";
import { Badge } from "@/components/ui/Badge";
import { Button } from "@/components/ui/Button";
import { ConfirmDialog } from "@/components/ui/ConfirmDialog";
import { useToast } from "@/components/ui/Toast";
import { errorMessage } from "@/lib/api-client";
import { useDeleteArticle } from "../../hooks/useSaveArticle";
import type { Article } from "../../types/article.types";
import { ArticleTypeBadge } from "../ArticleTypeBadge";

export function ArticleHeader({ article, canWrite }: { article: Article; canWrite: boolean }) {
  const router = useRouter();
  const toast = useToast();
  const remove = useDeleteArticle();
  const [confirm, setConfirm] = useState(false);

  const onDelete = async () => {
    try {
      await remove.mutateAsync(article.id);
      toast.success(`Article ${article.code} supprimé`);
      router.replace("/articles");
    } catch (e) {
      toast.error(errorMessage(e));
      setConfirm(false);
    }
  };

  return (
    <>
      <PageHeader
        title={`${article.code} – ${article.designation}`}
        breadcrumbs={[{ label: "Articles", href: "/articles" }, { label: article.code }]}
        subtitle={
          <>
            <ArticleTypeBadge type={article.type} />
            {article.actif ? <Badge tone="success">Actif</Badge> : <Badge>Inactif</Badge>}
          </>
        }
        actions={canWrite && <Button variant="ghost" onClick={() => setConfirm(true)}>Supprimer</Button>}
      />
      <ConfirmDialog
        open={confirm}
        title="Supprimer l'article"
        message={`L'article ${article.code} sera désactivé et masqué. Les documents existants ne sont pas modifiés.`}
        confirmLabel="Supprimer l'article"
        danger
        loading={remove.isPending}
        onConfirm={onDelete}
        onCancel={() => setConfirm(false)}
      />
    </>
  );
}
