"use client";

import type { UseFormReturn } from "react-hook-form";
import { FormGrid } from "@/components/layout/FormGrid";
import { FormField } from "@/components/ui/FormField";
import { Input } from "@/components/ui/Input";
import type { ArticleFormValues } from "../../schemas/article.schema";

export function ArticleFormStock({ form }: { form: UseFormReturn<ArticleFormValues> }) {
  const { register, watch, formState: { errors } } = form;
  const service = watch("type") === "SERVICE";
  return (
    <FormGrid columns={4}>
      <FormField
        label="Stock minimum (alerte)"
        htmlFor="a-min"
        error={errors.stockMinimum?.message}
        hint={service ? "Sans objet pour un service" : "Alerte quand le stock total passe en dessous"}
      >
        <Input id="a-min" type="number" step="0.001" min="0" align="right" disabled={service}
          {...register("stockMinimum", { valueAsNumber: true })} />
      </FormField>
    </FormGrid>
  );
}
