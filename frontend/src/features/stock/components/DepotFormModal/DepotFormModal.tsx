"use client";

import { zodResolver } from "@hookform/resolvers/zod";
import { useEffect } from "react";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { FormGrid, FormGridFull } from "@/components/layout/FormGrid";
import { Button } from "@/components/ui/Button";
import { Checkbox } from "@/components/ui/Checkbox";
import { FormField } from "@/components/ui/FormField";
import { Input } from "@/components/ui/Input";
import { Modal } from "@/components/ui/Modal";
import { useToast } from "@/components/ui/Toast";
import { errorMessage } from "@/lib/api-client";
import { useSaveDepot } from "../../hooks/useDepots";
import type { Depot } from "../../types/stock.types";

const schema = z.object({
  code: z.string().trim().min(1, "Code obligatoire").max(20, "20 caractères maximum"),
  libelle: z.string().trim().min(1, "Libellé obligatoire").max(100, "100 caractères maximum"),
  adresse: z.string().max(255).nullable(),
  actif: z.boolean(),
  parDefaut: z.boolean(),
});
type Values = z.infer<typeof schema>;

const EMPTY: Values = { code: "", libelle: "", adresse: null, actif: true, parDefaut: false };

export function DepotFormModal({ open, depot, onClose }: { open: boolean; depot: Depot | null; onClose: () => void }) {
  const toast = useToast();
  const save = useSaveDepot();
  const { register, handleSubmit, reset, formState: { errors } } = useForm<Values>({
    resolver: zodResolver(schema),
    mode: "onBlur",
    defaultValues: EMPTY,
  });

  useEffect(() => {
    if (open) reset(depot ? { code: depot.code, libelle: depot.libelle, adresse: depot.adresse, actif: depot.actif, parDefaut: depot.parDefaut } : EMPTY);
  }, [open, depot, reset]);

  const onSubmit = async (values: Values) => {
    try {
      await save.mutateAsync({ id: depot?.id, body: values });
      toast.success(depot ? "Dépôt modifié" : "Dépôt créé");
      onClose();
    } catch (e) {
      toast.error(errorMessage(e));
    }
  };

  return (
    <Modal
      open={open}
      onClose={onClose}
      title={depot ? `Modifier le dépôt ${depot.code}` : "Nouveau dépôt"}
      footer={
        <>
          <Button variant="ghost" onClick={onClose}>Annuler</Button>
          <Button variant="primary" loading={save.isPending} onClick={handleSubmit(onSubmit)}>Enregistrer</Button>
        </>
      }
    >
      <form onSubmit={handleSubmit(onSubmit)} noValidate>
        <FormGrid>
          <FormField label="Code" htmlFor="d-code" required error={errors.code?.message}>
            <Input id="d-code" invalid={!!errors.code} {...register("code")} />
          </FormField>
          <FormField label="Libellé" htmlFor="d-libelle" required error={errors.libelle?.message}>
            <Input id="d-libelle" invalid={!!errors.libelle} {...register("libelle")} />
          </FormField>
          <FormGridFull>
            <FormField label="Adresse" htmlFor="d-adresse">
              <Input id="d-adresse" {...register("adresse")} />
            </FormField>
          </FormGridFull>
          <Checkbox label="Dépôt actif" {...register("actif")} />
          <Checkbox label="Dépôt par défaut" {...register("parDefaut")} />
        </FormGrid>
        <button type="submit" hidden />
      </form>
    </Modal>
  );
}
