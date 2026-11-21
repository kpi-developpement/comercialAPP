import { errorMessage } from "@/lib/api-client";
import { Button } from "../Button";
import styles from "./ErrorState.module.css";

interface ErrorStateProps {
  error: unknown;
  onRetry?: () => void;
}

export function ErrorState({ error, onRetry }: ErrorStateProps) {
  return (
    <div className={styles.error} role="alert">
      <p className={styles.title}>Impossible de charger les données</p>
      <p className={styles.message}>{errorMessage(error)}</p>
      {onRetry && (
        <Button size="sm" onClick={onRetry}>
          Réessayer
        </Button>
      )}
    </div>
  );
}
