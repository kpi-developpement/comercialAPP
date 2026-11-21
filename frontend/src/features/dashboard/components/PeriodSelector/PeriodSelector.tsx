"use client";

import { Input } from "@/components/ui/Input";
import { Select } from "@/components/ui/Select";
import styles from "./PeriodSelector.module.css";

export interface Period {
  dateDebut: string;
  dateFin: string;
}

const pad = (n: number) => String(n).padStart(2, "0");
const iso = (d: Date) => `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`;

export const PRESETS: Record<string, () => Period> = {
  annee: () => {
    const y = new Date().getFullYear();
    return { dateDebut: `${y}-01-01`, dateFin: `${y}-12-31` };
  },
  mois: () => {
    const d = new Date();
    return { dateDebut: iso(new Date(d.getFullYear(), d.getMonth(), 1)), dateFin: iso(new Date(d.getFullYear(), d.getMonth() + 1, 0)) };
  },
  trimestre: () => {
    const d = new Date();
    const q = Math.floor(d.getMonth() / 3) * 3;
    return { dateDebut: iso(new Date(d.getFullYear(), q, 1)), dateFin: iso(new Date(d.getFullYear(), q + 3, 0)) };
  },
  douze: () => {
    const d = new Date();
    return { dateDebut: iso(new Date(d.getFullYear(), d.getMonth() - 11, 1)), dateFin: iso(new Date(d.getFullYear(), d.getMonth() + 1, 0)) };
  },
};

const OPTIONS = [
  { value: "annee", label: "Année en cours" },
  { value: "trimestre", label: "Trimestre en cours" },
  { value: "mois", label: "Mois en cours" },
  { value: "douze", label: "12 derniers mois" },
  { value: "perso", label: "Période personnalisée" },
];

interface Props {
  preset: string;
  period: Period;
  onChange: (preset: string, period: Period) => void;
}

export function PeriodSelector({ preset, period, onChange }: Props) {
  return (
    <div className={styles.selector}>
      <Select aria-label="Période" options={OPTIONS} value={preset}
        onChange={(e) => onChange(e.target.value, PRESETS[e.target.value]?.() ?? period)} />
      {preset === "perso" && (
        <>
          <Input type="date" aria-label="Du" value={period.dateDebut} onChange={(e) => onChange("perso", { ...period, dateDebut: e.target.value })} />
          <Input type="date" aria-label="Au" value={period.dateFin} onChange={(e) => onChange("perso", { ...period, dateFin: e.target.value })} />
        </>
      )}
    </div>
  );
}
