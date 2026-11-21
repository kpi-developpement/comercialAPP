"use client";

import { useState } from "react";
import { PageHeader } from "@/components/layout/PageHeader";
import { Tabs } from "@/components/ui/Tabs";
import { RequireAuth } from "@/features/auth/components/RequireAuth";
import { ModesReglementPanel } from "../ModesReglementPanel";
import { NumerotationPanel } from "../NumerotationPanel";
import { SocieteForm } from "../SocieteForm";
import { TauxTvaPanel } from "../TauxTvaPanel";
import styles from "./ParametresView.module.css";

type Tab = "societe" | "tva" | "modes" | "numerotation";

const TABS: { key: Tab; label: string }[] = [
  { key: "societe", label: "Société" },
  { key: "tva", label: "Taux de TVA" },
  { key: "modes", label: "Modes de règlement" },
  { key: "numerotation", label: "Numérotation" },
];

export function ParametresView() {
  const [tab, setTab] = useState<Tab>("societe");
  return (
    <RequireAuth permission="admin">
      <PageHeader title="Paramètres" />
      <Tabs items={TABS} active={tab} onChange={setTab} />
      <div className={styles.panel}>
        {tab === "societe" && <SocieteForm />}
        {tab === "tva" && <TauxTvaPanel />}
        {tab === "modes" && <ModesReglementPanel />}
        {tab === "numerotation" && <NumerotationPanel />}
      </div>
    </RequireAuth>
  );
}
