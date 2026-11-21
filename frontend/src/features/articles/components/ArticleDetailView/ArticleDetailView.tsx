"use client";

import { ErrorState } from "@/components/ui/ErrorState";
import { Skeleton } from "@/components/ui/Skeleton";
import { useAuth } from "@/features/auth/components/AuthProvider";
import { ArticleStockPanel } from "@/features/stock/components/ArticleStockPanel";
import { useArticle } from "../../hooks/useArticle";
import { ArticleForm } from "../ArticleForm";
import { ArticleHeader } from "../ArticleHeader";
import styles from "./ArticleDetailView.module.css";

export function ArticleDetailView({ id }: { id: number }) {
  const { can } = useAuth();
  const query = useArticle(id);
  const canWrite = can("article.write");

  if (query.isLoading) return <Skeleton height={320} />;
  if (query.error || !query.data) return <ErrorState error={query.error} onRetry={() => query.refetch()} />;
  const article = query.data;

  return (
    <>
      <ArticleHeader article={article} canWrite={canWrite} />
      <div className={styles.layout}>
        <ArticleForm article={article} readOnly={!canWrite} />
        {article.type === "STOCKE" && <ArticleStockPanel article={article} />}
      </div>
    </>
  );
}
