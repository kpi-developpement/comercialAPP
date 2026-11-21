"use client";

import Link from "next/link";
import { PageHeader } from "@/components/layout/PageHeader";
import { Button } from "@/components/ui/Button";
import { Card } from "@/components/ui/Card";
import { EmptyState } from "@/components/ui/EmptyState";
import { Icon } from "@/components/ui/Icon";
import { Pagination } from "@/components/ui/Pagination";
import { useAuth } from "@/features/auth/components/AuthProvider";
import { useDebouncedValue } from "@/lib/use-debounced-value";
import { useListParams } from "@/lib/use-list-params";
import { useArticles } from "../../hooks/useArticles";
import type { ArticleFilters } from "../../types/article.types";
import { ArticlesFilters } from "../ArticlesFilters";
import { ArticlesTable } from "../ArticlesTable";

const INITIAL: ArticleFilters = { search: "", familleId: "", type: "", actif: "" };

export function ArticlesListView() {
  const { can } = useAuth();
  const list = useListParams<ArticleFilters>(INITIAL, "code,asc");
  const search = useDebouncedValue(list.filters.search);
  const query = useArticles({ ...list.params, search });
  const hasFilters = Object.values(list.filters).some((v) => v !== "");

  return (
    <>
      <PageHeader
        title="Articles"
        subtitle={query.data ? `${query.data.totalElements} article(s)` : undefined}
        actions={
          can("article.write") && (
            <Link href="/articles/nouveau">
              <Button variant="primary" icon={<Icon name="plus" size={16} />}>Nouvel article</Button>
            </Link>
          )
        }
      />
      <Card padded={false}>
        <ArticlesFilters filters={list.filters} onChange={list.updateFilter} />
        <ArticlesTable
          rows={query.data?.content}
          loading={query.isLoading}
          error={query.error}
          onRetry={() => query.refetch()}
          sort={list.sort}
          onSort={list.toggleSort}
          empty={
            hasFilters ? (
              <EmptyState
                title="Aucun article ne correspond aux filtres"
                action={<Button onClick={() => list.setFilters(INITIAL)}>Effacer les filtres</Button>}
              />
            ) : (
              <EmptyState title="Aucun article" description="Créez votre premier article pour commencer à vendre." />
            )
          }
          footer={
            query.data && (
              <Pagination
                page={query.data.page}
                totalPages={query.data.totalPages}
                totalElements={query.data.totalElements}
                size={list.size}
                onPageChange={list.setPage}
                onSizeChange={list.setSize}
              />
            )
          }
        />
      </Card>
    </>
  );
}
