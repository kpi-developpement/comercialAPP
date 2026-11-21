"use client";

import type { UseFormReturn } from "react-hook-form";
import { FormGrid } from "@/components/layout/FormGrid";
import { Checkbox } from "@/components/ui/Checkbox";
import { FormField } from "@/components/ui/FormField";
import { Input } from "@/components/ui/Input";
import { Select } from "@/components/ui/Select";
import { useFamilleOptions } from "@/features/familles/hooks/useFamilles";
import type { ArticleFormValues } from "../../schemas/article.schema";

export function ArticleFormGeneral({ form }: { form: UseFormReturn<ArticleFormValues> }) {
  const { register, formState: { errors } } = form;
  const { data: familles = [] } = useFamilleOptions();
  return (
    <FormGrid columns={3}>
      <FormField label="Code" htmlFor="a-code" required error={errors.code?.message}>
        <Input id="a-code" invalid={!!errors.code} {...register("code")} />
      </FormField>
      <FormField label="Désignation" htmlFor="a-designation" required error={errors.designation?.message}>
        <Input id="a-designation" invalid={!!errors.designation} {...register("designation")} />
      </FormField>
      <FormField label="Famille" htmlFor="a-famille">
        <Select
          id="a-famille"
          options={familles}
          placeholder="Sans famille"
          {...register("familleId", { setValueAs: (v) => (v === "" || v === null ? null : Number(v)) })}
        />
      </FormField>
      <FormField label="Type" htmlFor="a-type" required>
        <Select
          id="a-type"
          options={[
            { value: "STOCKE", label: "Produit stocké" },
            { value: "SERVICE", label: "Service (non stocké)" },
          ]}
          {...register("type")}
        />
      </FormField>
      <FormField label="Code-barres" htmlFor="a-barcode" hint="EAN-13, unique" error={errors.codeBarres?.message}>
        <Input id="a-barcode" {...register("codeBarres", { setValueAs: (v) => (v ? String(v) : null) })} />
      </FormField>
      <FormField label="Statut" htmlFor="a-actif">
        <Checkbox id="a-actif" label="Article actif" {...register("actif")} />
      </FormField>
    </FormGrid>
  );
}
