"use client";

import { zodResolver } from "@hookform/resolvers/zod";
import { useRouter } from "next/navigation";
import { useForm } from "react-hook-form";
import { Button } from "@/components/ui/Button";
import { Card } from "@/components/ui/Card";
import { useToast } from "@/components/ui/Toast";
import { useTauxTva } from "@/features/parametres/hooks/useParametres";
import { errorMessage } from "@/lib/api-client";
import { useSaveArticle } from "../../hooks/useSaveArticle";
import { articleSchema, type ArticleFormValues } from "../../schemas/article.schema";
import type { Article } from "../../types/article.types";
import { ArticleFormGeneral } from "./ArticleFormGeneral";
import { ArticleFormPricing } from "./ArticleFormPricing";
import { ArticleFormStock } from "./ArticleFormStock";
import styles from "./ArticleForm.module.css";

interface ArticleFormProps {
  article?: Article;
  readOnly?: boolean;
}

function toValues(article: Article | undefined, defaultTva: number): ArticleFormValues {
  if (!article) {
    return {
      code: "", designation: "", familleId: null, unite: "U", prixAchatHt: 0, prixVenteHt: 0,
      tauxTva: defaultTva, codeBarres: null, stockMinimum: 0, actif: true, type: "STOCKE",
    };
  }
  return {
    code: article.code, designation: article.designation, familleId: article.familleId, unite: article.unite,
    prixAchatHt: Number(article.prixAchatHt), prixVenteHt: Number(article.prixVenteHt), tauxTva: Number(article.tauxTva),
    codeBarres: article.codeBarres, stockMinimum: Number(article.stockMinimum), actif: article.actif, type: article.type,
  };
}

export function ArticleForm({ article, readOnly = false }: ArticleFormProps) {
  const router = useRouter();
  const toast = useToast();
  const save = useSaveArticle();
  const { data: taux = [] } = useTauxTva();
  const defaultTva = Number(taux.find((t) => t.parDefaut)?.taux ?? 20);
  const form = useForm<ArticleFormValues>({
    resolver: zodResolver(articleSchema),
    mode: "onBlur",
    values: toValues(article, defaultTva),
    resetOptions: { keepDirtyValues: true },
  });

  const onSubmit = async (values: ArticleFormValues) => {
    try {
      const saved = await save.mutateAsync({
        id: article?.id,
        body: { ...values, codeBarres: values.codeBarres ? values.codeBarres : null },
      });
      toast.success(article ? "Article enregistré" : `Article ${saved.code} créé`);
      if (!article) router.replace(`/articles/${saved.id}`);
    } catch (e) {
      toast.error(errorMessage(e));
    }
  };

  return (
    <form className={styles.form} onSubmit={form.handleSubmit(onSubmit)} noValidate>
      <fieldset disabled={readOnly} className={styles.fieldset}>
        <Card title="Informations générales">
          <ArticleFormGeneral form={form} />
        </Card>
        <Card title="Prix et TVA">
          <ArticleFormPricing form={form} taux={taux} />
        </Card>
        <Card title="Stock">
          <ArticleFormStock form={form} />
        </Card>
      </fieldset>
      {!readOnly && (
        <div className={styles.actions}>
          <Button variant="ghost" onClick={() => router.push("/articles")}>Retour à la liste</Button>
          <Button type="submit" variant="primary" loading={save.isPending}>
            {article ? "Enregistrer les modifications" : "Créer l'article"}
          </Button>
        </div>
      )}
    </form>
  );
}
