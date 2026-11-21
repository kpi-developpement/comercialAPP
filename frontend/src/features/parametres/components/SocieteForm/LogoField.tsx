"use client";

import { useState } from "react";
import { Button } from "@/components/ui/Button";
import styles from "./SocieteForm.module.css";

const MAX_BYTES = 500 * 1024;

/** Logo upload stored as a data URL (PNG / JPEG, 500 Ko max). */
export function LogoField({ value, onChange }: { value: string | null; onChange: (v: string | null) => void }) {
  const [error, setError] = useState<string | null>(null);

  const onFile = (file: File | undefined) => {
    setError(null);
    if (!file) return;
    if (!["image/png", "image/jpeg"].includes(file.type)) {
      setError("Format accepté : PNG ou JPEG");
      return;
    }
    if (file.size > MAX_BYTES) {
      setError("Image trop volumineuse (500 Ko maximum)");
      return;
    }
    const reader = new FileReader();
    reader.onload = () => onChange(String(reader.result));
    reader.readAsDataURL(file);
  };

  return (
    <div className={styles.logo}>
      <span className={styles.logoLabel}>Logo</span>
      <div className={styles.logoRow}>
        <div className={styles.logoPreview}>
          {value ? <img src={value} alt="Logo de la société" /> : <span>Aucun logo</span>}
        </div>
        <label className={styles.upload}>
          <input type="file" accept="image/png,image/jpeg" onChange={(e) => onFile(e.target.files?.[0])} />
          <span>Choisir une image</span>
        </label>
        {value && <Button size="sm" variant="ghost" onClick={() => onChange(null)}>Retirer le logo</Button>}
      </div>
      {error && <p className={styles.error} role="alert">{error}</p>}
    </div>
  );
}
