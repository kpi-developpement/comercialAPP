"use client";

import { useEffect, useState, type Ref } from "react";
import { Combobox } from "@/components/ui/Combobox";
import { useToast } from "@/components/ui/Toast";
import { articlesApi } from "@/features/articles/api/articles.api";
import { useArticles } from "@/features/articles/hooks/useArticles";
import type { Article, ArticleSummary } from "@/features/articles/types/article.types";
import { errorMessage } from "@/lib/api-client";
import { formatMoney } from "@/lib/format";
import { useDebouncedValue } from "@/lib/use-debounced-value";

interface ArticlePickerProps {
  /** Label of the current selection (code). */
  label: string;
  onSelect: (article: ArticleSummary | Article) => void;
  /** Restrict to stocked articles (inventaires, transferts). */
  stockedOnly?: boolean;
  priceField?: "prixVenteHt" | "prixAchatHt";
  inputRef?: Ref<HTMLInputElement>;
  invalid?: boolean;
  ariaLabel?: string;
}

/**
 * Search by code / désignation / code-barres. Pressing Enter on an exact code or barcode selects it directly
 * (barcode scanners type the code followed by Enter).
 */
export function ArticlePicker({
  label,
  onSelect,
  stockedOnly,
  priceField = "prixVenteHt",
  inputRef,
  invalid,
  ariaLabel = "Article",
}: ArticlePickerProps) {
  const toast = useToast();
  const [query, setQuery] = useState(label);
  useEffect(() => setQuery(label), [label]);
  const search = useDebouncedValue(query, 250);
  const typing = query !== label && query.trim().length > 0;
  const { data, isFetching } = useArticles(
    { page: 0, size: 12, search, actif: true, type: stockedOnly ? "STOCKE" : undefined, sort: "code,asc" },
    typing,
  );

  const select = (article: ArticleSummary | Article) => {
    onSelect(article);
    setQuery(article.code);
  };

  const scan = async (value: string) => {
    try {
      select(await articlesApi.scan(value));
    } catch (e) {
      toast.error(errorMessage(e));
    }
  };

  return (
    <Combobox
      ariaLabel={ariaLabel}
      placeholder="Code, désignation ou code-barres"
      query={query}
      onQueryChange={setQuery}
      inputRef={inputRef}
      invalid={invalid}
      loading={typing && isFetching}
      items={typing ? (data?.content ?? []).map((a) => ({
        key: a.id,
        value: a,
        primary: `${a.code} – ${a.designation}`,
        secondary: formatMoney(a[priceField]),
      })) : []}
      onSelect={select}
      onEnterWithoutSelection={scan}
    />
  );
}
