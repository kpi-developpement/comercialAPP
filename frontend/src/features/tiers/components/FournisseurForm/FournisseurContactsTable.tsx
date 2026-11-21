"use client";

import { useFieldArray, type UseFormReturn } from "react-hook-form";
import { Button } from "@/components/ui/Button";
import { Icon } from "@/components/ui/Icon";
import { Input } from "@/components/ui/Input";
import type { FournisseurFormValues } from "../../schemas/fournisseur.schema";
import styles from "./FournisseurForm.module.css";

const COLUMNS = [
  { name: "nom", label: "Nom", type: "text" },
  { name: "prenom", label: "Prénom", type: "text" },
  { name: "fonction", label: "Fonction", type: "text" },
  { name: "telephone", label: "Téléphone", type: "tel" },
  { name: "email", label: "E-mail", type: "email" },
] as const;

/** Editable grid of supplier contacts ("interlocuteurs"). */
export function FournisseurContactsTable({ form, readOnly }: { form: UseFormReturn<FournisseurFormValues>; readOnly: boolean }) {
  const { fields, append, remove } = useFieldArray({ control: form.control, name: "contacts" });
  const errors = form.formState.errors.contacts;

  return (
    <div>
      <div className={styles.scroll}>
        <table className={styles.table}>
          <thead>
            <tr>
              {COLUMNS.map((c) => <th key={c.name}>{c.label}</th>)}
              <th />
            </tr>
          </thead>
          <tbody>
            {fields.map((field, i) => (
              <tr key={field.id}>
                {COLUMNS.map((c) => (
                  <td key={c.name}>
                    <Input
                      type={c.type}
                      aria-label={`${c.label} interlocuteur ${i + 1}`}
                      invalid={!!errors?.[i]?.[c.name]}
                      title={errors?.[i]?.[c.name]?.message}
                      {...form.register(`contacts.${i}.${c.name}`)}
                    />
                  </td>
                ))}
                <td className={styles.remove}>
                  {!readOnly && (
                    <Button size="sm" variant="ghost" aria-label={`Supprimer l'interlocuteur ${i + 1}`} onClick={() => remove(i)}>
                      <Icon name="trash" size={14} />
                    </Button>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
      {fields.length === 0 && <p className={styles.empty}>Aucun interlocuteur.</p>}
      {!readOnly && (
        <div className={styles.addRow}>
          <Button size="sm" icon={<Icon name="plus" size={14} />}
            onClick={() => append({ id: null, nom: "", prenom: "", fonction: "", telephone: "", email: "" })}>
            Ajouter un interlocuteur
          </Button>
        </div>
      )}
    </div>
  );
}
