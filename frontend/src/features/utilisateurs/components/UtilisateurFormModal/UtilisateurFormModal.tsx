"use client";

import { zodResolver } from "@hookform/resolvers/zod";
import { useEffect } from "react";
import { useForm } from "react-hook-form";
import { FormGrid } from "@/components/layout/FormGrid";
import { Button } from "@/components/ui/Button";
import { Checkbox } from "@/components/ui/Checkbox";
import { FormField } from "@/components/ui/FormField";
import { Input } from "@/components/ui/Input";
import { Modal } from "@/components/ui/Modal";
import { Select } from "@/components/ui/Select";
import { useToast } from "@/components/ui/Toast";
import { errorMessage } from "@/lib/api-client";
import { ROLE_LABELS, type Role } from "@/lib/permissions";
import { useCreateUtilisateur, useUpdateUtilisateur } from "../../hooks/useUtilisateurs";
import { utilisateurCreateSchema, utilisateurUpdateSchema, type UtilisateurFormValues } from "../../schemas/utilisateur.schema";
import type { Utilisateur } from "../../types/utilisateur.types";

const ROLES = (Object.keys(ROLE_LABELS) as Role[]).map((r) => ({ value: r, label: ROLE_LABELS[r] }));

interface Props {
  open: boolean;
  utilisateur: Utilisateur | null;
  onClose: () => void;
}

export function UtilisateurFormModal({ open, utilisateur, onClose }: Props) {
  const toast = useToast();
  const create = useCreateUtilisateur();
  const update = useUpdateUtilisateur();
  const editing = !!utilisateur;
  const { register, handleSubmit, reset, formState: { errors } } = useForm<UtilisateurFormValues>({
    resolver: zodResolver(editing ? utilisateurUpdateSchema : utilisateurCreateSchema),
    mode: "onBlur",
  });

  useEffect(() => {
    if (!open) return;
    reset(utilisateur
      ? { username: utilisateur.username, nomComplet: utilisateur.nomComplet, email: utilisateur.email ?? "", role: utilisateur.role, actif: utilisateur.actif, motDePasse: "" }
      : { username: "", nomComplet: "", email: "", role: "COMMERCIAL", actif: true, motDePasse: "" });
  }, [open, utilisateur, reset]);

  const onSubmit = async (v: UtilisateurFormValues) => {
    try {
      const common = { nomComplet: v.nomComplet, email: v.email || null, role: v.role, actif: v.actif };
      if (utilisateur) await update.mutateAsync({ id: utilisateur.id, body: common });
      else await create.mutateAsync({ ...common, username: v.username, motDePasse: v.motDePasse });
      toast.success(utilisateur ? "Utilisateur modifié" : "Utilisateur créé");
      onClose();
    } catch (e) {
      toast.error(errorMessage(e));
    }
  };

  return (
    <Modal
      open={open}
      onClose={onClose}
      title={utilisateur ? `Modifier ${utilisateur.username}` : "Nouvel utilisateur"}
      footer={
        <>
          <Button variant="ghost" onClick={onClose}>Annuler</Button>
          <Button variant="primary" onClick={handleSubmit(onSubmit)} loading={create.isPending || update.isPending}>Enregistrer</Button>
        </>
      }
    >
      <form onSubmit={handleSubmit(onSubmit)} noValidate>
        <FormGrid>
          <FormField label="Identifiant" htmlFor="u-username" required error={errors.username?.message}>
            <Input id="u-username" disabled={editing} invalid={!!errors.username} {...register("username")} />
          </FormField>
          <FormField label="Nom complet" htmlFor="u-nom" required error={errors.nomComplet?.message}>
            <Input id="u-nom" invalid={!!errors.nomComplet} {...register("nomComplet")} />
          </FormField>
          <FormField label="E-mail" htmlFor="u-email" error={errors.email?.message}>
            <Input id="u-email" type="email" invalid={!!errors.email} {...register("email")} />
          </FormField>
          <FormField label="Rôle" htmlFor="u-role" required>
            <Select id="u-role" options={ROLES} {...register("role")} />
          </FormField>
          {!editing && (
            <FormField label="Mot de passe" htmlFor="u-pwd" required error={errors.motDePasse?.message} hint="8 caractères minimum">
              <Input id="u-pwd" type="password" autoComplete="new-password" invalid={!!errors.motDePasse} {...register("motDePasse")} />
            </FormField>
          )}
          <Checkbox label="Compte actif" {...register("actif")} />
        </FormGrid>
        <button type="submit" hidden />
      </form>
    </Modal>
  );
}
