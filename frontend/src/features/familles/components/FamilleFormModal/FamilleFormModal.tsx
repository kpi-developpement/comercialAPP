"use client";

import { zodResolver } from "@hookform/resolvers/zod";
import { useEffect } from "react";
import { useForm } from "react-hook-form";
import { FormGrid } from "@/components/layout/FormGrid";
import { Button } from "@/components/ui/Button";
import { FormField } from "@/components/ui/FormField";
import { Input } from "@/components/ui/Input";
import { Modal } from "@/components/ui/Modal";
import { Select } from "@/components/ui/Select";
import { useToast } from "@/components/ui/Toast";
import { errorMessage } from "@/lib/api-client";
import { useRootFamilleOptions, useSaveFamille } from "../../hooks/useFamilles";
import { familleSchema, type FamilleFormValues } from "../../schemas/famille.schema";
import type { Famille } from "../../types/famille.types";

interface FamilleFormModalProps {
  open: boolean;
  famille: Famille | null;
  onClose: () => void;
}

const EMPTY: FamilleFormValues = { code: "", libelle: "", parentId: null };

export function FamilleFormModal({ open, famille, onClose }: FamilleFormModalProps) {
  const toast = useToast();
  const save = useSaveFamille();
  const { data: parents = [] } = useRootFamilleOptions(famille?.id);
  const { register, handleSubmit, reset, formState: { errors } } = useForm<FamilleFormValues>({
    resolver: zodResolver(familleSchema),
    mode: "onBlur",
    defaultValues: EMPTY,
  });

  useEffect(() => {
    if (open) reset(famille ? { code: famille.code, libelle: famille.libelle, parentId: famille.parentId } : EMPTY);
  }, [open, famille, reset]);

  const onSubmit = async (values: FamilleFormValues) => {
    try {
      await save.mutateAsync({ id: famille?.id, body: values });
      toast.success(famille ? "Famille modifiée" : "Famille créée");
      onClose();
    } catch (e) {
      toast.error(errorMessage(e));
    }
  };

  return (
    <Modal
      open={open}
      onClose={onClose}
      title={famille ? `Modifier la famille ${famille.code}` : "Nouvelle famille"}
      footer={
        <>
          <Button variant="ghost" onClick={onClose}>Annuler</Button>
          <Button variant="primary" loading={save.isPending} onClick={handleSubmit(onSubmit)}>
            Enregistrer
          </Button>
        </>
      }
    >
      <form onSubmit={handleSubmit(onSubmit)} noValidate>
        <FormGrid>
          <FormField label="Code" htmlFor="f-code" required error={errors.code?.message}>
            <Input id="f-code" invalid={!!errors.code} {...register("code")} />
          </FormField>
          <FormField label="Libellé" htmlFor="f-libelle" required error={errors.libelle?.message}>
            <Input id="f-libelle" invalid={!!errors.libelle} {...register("libelle")} />
          </FormField>
          <FormField label="Famille parente" htmlFor="f-parent" hint="Laisser vide pour une famille principale">
            <Select
              id="f-parent"
              options={parents}
              placeholder="Aucune (famille principale)"
              {...register("parentId", { setValueAs: (v) => (v === "" || v === null ? null : Number(v)) })}
            />
          </FormField>
        </FormGrid>
        <button type="submit" hidden />
      </form>
    </Modal>
  );
}
