import { Suspense } from "react";
import { LoginForm } from "@/features/auth/components/LoginForm";
import { LoginHero } from "@/features/auth/components/LoginHero";
import styles from "./page.module.css";

export default function LoginPage() {
  return (
    <main className={styles.page}>
      <div className={styles.layout}>
        <LoginHero />
        <Suspense>
          <LoginForm />
        </Suspense>
      </div>
    </main>
  );
}
