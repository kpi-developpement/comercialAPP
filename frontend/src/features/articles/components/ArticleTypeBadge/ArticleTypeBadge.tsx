import { Badge } from "@/components/ui/Badge";
import { ARTICLE_TYPE_LABELS, type ArticleType } from "../../types/article.types";

export function ArticleTypeBadge({ type }: { type: ArticleType }) {
  return <Badge tone={type === "STOCKE" ? "primary" : "info"}>{ARTICLE_TYPE_LABELS[type]}</Badge>;
}
