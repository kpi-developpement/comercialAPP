"use client";

import { useEffect, useState } from "react";
import { Button } from "@/components/ui/Button";
import { FormField } from "@/components/ui/FormField";
import { Input } from "@/components/ui/Input";
import { Modal } from "@/components/ui/Modal";
import { useToast } from "@/components/ui/Toast";
import { errorMessage } from "@/lib/api-client";
import { useResetPassword } from "../../hooks/useUtilisateurs";
import type { Utilisateur } from "../../types/utilisateur.types";

export function ResetPasswordModal({ utilisateur, onClose }: { utilisateur: Utilisateur | null; onClose: () => void }) {
  const toast = useToast();
  const reset = useResetPassword();
  const [password, setPassword] = useState("");
  const [touched, setTouched] = useState(false);

  useEffect(() => {
    setPassword("");
    setTouched(false);
  }, [utilisateur]);

  const error = touched && password.length < 8 ? "8 caractères minimum" : undefined;

  const submit = async () => {
    setTouched(true);
    if (!utilisateur || password.length < 8) return;
    try {
      await reset.mutateAsync({ id: utilisateur.id, password });
      toast.success(`Mot de passe de ${utilisateur.username} réinitialisé`);
      onClose();
    } catch (e) {
      toast.error(errorMessage(e));
    }
  };

  return (
    <Modal
      open={!!utilisateur}
      onClose={onClose}
      size="sm"
      title={`Réinitialiser le mot de passe`}
      footer={
        <>
          <Button variant="ghost" onClick={onClose}>Annuler</Button>
          <Button variant="primary" onClick={submit} loading={reset.isPending}>Réinitialiser le mot de passe</Button>
        </>
      }
    >
      <FormField label={`Nouveau mot de passe pour ${utilisateur?.username ?? ""}`} htmlFor="rp-pwd" error={error}>
        <Input id="rp-pwd" type="password" autoComplete="new-password" value={password} invalid={!!error}
          onChange={(e) => setPassword(e.target.value)} onBlur={() => setTouched(true)} />
      </FormField>
    </Modal>
  );
}
