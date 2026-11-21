"use client";

import { useRouter } from "next/navigation";
import { useEffect, type ReactNode } from "react";
import { Spinner } from "@/components/ui/Spinner";
import type { Permission } from "@/lib/permissions";
import { useAuth } from "../AuthProvider";
import styles from "./RequireAuth.module.css";

interface RequireAuthProps {
  permission?: Permission;
  children: ReactNode;
}

/** Redirects to /login when not authenticated; shows a notice when the role lacks the permission. */
export function RequireAuth({ permission, children }: RequireAuthProps) {
  const { user, ready, can } = useAuth();
  const router = useRouter();

  useEffect(() => {
    if (ready && !user) router.replace("/login");
  }, [ready, user, router]);

  if (!ready || !user) {
    return (
      <div className={styles.center}>
        <Spinner size="lg" />
      </div>
    );
  }
  if (permission && !can(permission)) {
    return (
      <div className={styles.center}>
        <p className={styles.denied}>Votre rôle ne donne pas accès à cette page.</p>
      </div>
    );
  }
  return <>{children}</>;
}
