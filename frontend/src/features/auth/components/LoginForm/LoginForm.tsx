"use client";

import { zodResolver } from "@hookform/resolvers/zod";
import { useRouter, useSearchParams } from "next/navigation";
import { useRef, useState } from "react";
import { useForm } from "react-hook-form";
import { Button } from "@/components/ui/Button";
import { FormField } from "@/components/ui/FormField";
import { Input } from "@/components/ui/Input";
import { errorMessage } from "@/lib/api-client";
import { getStoredUser } from "@/lib/auth";
import { useTilt } from "@/lib/motion/useTilt";
import { homeFor } from "@/lib/permissions";
import { loginSchema, type LoginFormValues } from "../../schemas/login.schema";
import { useAuth } from "../AuthProvider";
import styles from "./LoginForm.module.css";

export function LoginForm() {
  const { login } = useAuth();
  const router = useRouter();
  const searchParams = useSearchParams();
  const formRef = useRef<HTMLFormElement>(null);
  useTilt(formRef, 4);
  const [serverError, setServerError] = useState<string | null>(
    searchParams.get("expired") ? "Votre session a expiré, reconnectez-vous." : null,
  );
  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<LoginFormValues>({ resolver: zodResolver(loginSchema), mode: "onBlur" });

  const onSubmit = async (values: LoginFormValues) => {
    setServerError(null);
    try {
      await login(values);
      router.replace(homeFor(getStoredUser()?.role));
    } catch (e) {
      setServerError(errorMessage(e));
    }
  };

  return (
    <form ref={formRef} className={styles.form} onSubmit={handleSubmit(onSubmit)} noValidate>
      <span className={styles.glare} aria-hidden />
      <div className={styles.brand}>
        <span className={styles.logo} aria-hidden />
        <div>
          <h1 className={styles.title}>Connexion</h1>
          <p className={styles.subtitle}>Accédez à votre espace GestCom</p>
        </div>
      </div>
      {serverError && (
        <p className={styles.serverError} role="alert">
          {serverError}
        </p>
      )}
      <FormField label="Identifiant" htmlFor="username" error={errors.username?.message}>
        <Input id="username" autoComplete="username" autoFocus invalid={!!errors.username} {...register("username")} />
      </FormField>
      <FormField label="Mot de passe" htmlFor="password" error={errors.password?.message}>
        <Input
          id="password"
          type="password"
          autoComplete="current-password"
          invalid={!!errors.password}
          {...register("password")}
        />
      </FormField>
      <Button type="submit" variant="primary" loading={isSubmitting} className={styles.submit}>
        Se connecter
      </Button>
    </form>
  );
}
