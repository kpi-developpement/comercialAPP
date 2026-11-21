"use client";

import type { FieldValues, Path, UseFormReturn } from "react-hook-form";
import type { Option } from "@/lib/types";
import { Checkbox } from "../Checkbox";
import { FormField } from "../FormField";
import { Input } from "../Input";
import { Select } from "../Select";
import { Textarea } from "../Textarea";

/**
 * react-hook-form bound fields (label + control + inline error) to keep long forms declarative.
 */
interface BaseProps<T extends FieldValues> {
  form: UseFormReturn<T>;
  name: Path<T>;
  label: string;
  required?: boolean;
  hint?: string;
  disabled?: boolean;
}

function errorOf<T extends FieldValues>(form: UseFormReturn<T>, name: Path<T>): string | undefined {
  const parts = String(name).split(".");
  let node: unknown = form.formState.errors;
  for (const p of parts) node = (node as Record<string, unknown> | undefined)?.[p];
  return (node as { message?: string } | undefined)?.message;
}

const fieldId = (name: string) => `f-${name.replace(/\./g, "-")}`;

export function TextField<T extends FieldValues>({ form, name, label, required, hint, disabled, type = "text" }:
  BaseProps<T> & { type?: "text" | "email" | "tel" | "url" | "password" }) {
  const error = errorOf(form, name);
  return (
    <FormField label={label} htmlFor={fieldId(name)} required={required} hint={hint} error={error}>
      <Input id={fieldId(name)} type={type} invalid={!!error} disabled={disabled} {...form.register(name)} />
    </FormField>
  );
}

export function NumberField<T extends FieldValues>({ form, name, label, required, hint, disabled, step = "0.01" }:
  BaseProps<T> & { step?: string }) {
  const error = errorOf(form, name);
  return (
    <FormField label={label} htmlFor={fieldId(name)} required={required} hint={hint} error={error}>
      <Input id={fieldId(name)} type="number" min="0" step={step} align="right" invalid={!!error} disabled={disabled}
        {...form.register(name, { valueAsNumber: true })} />
    </FormField>
  );
}

export function DateField<T extends FieldValues>({ form, name, label, required, hint, disabled }: BaseProps<T>) {
  const error = errorOf(form, name);
  return (
    <FormField label={label} htmlFor={fieldId(name)} required={required} hint={hint} error={error}>
      <Input id={fieldId(name)} type="date" invalid={!!error} disabled={disabled} {...form.register(name)} />
    </FormField>
  );
}

export function TextAreaField<T extends FieldValues>({ form, name, label, hint, disabled, rows = 3 }: BaseProps<T> & { rows?: number }) {
  return (
    <FormField label={label} htmlFor={fieldId(name)} hint={hint} error={errorOf(form, name)}>
      <Textarea id={fieldId(name)} rows={rows} disabled={disabled} {...form.register(name)} />
    </FormField>
  );
}

/**
 * @param numeric value stored as a number (null when empty)
 * @param nullable value stored as a string, null when the placeholder is chosen
 */
export function SelectField<T extends FieldValues>({ form, name, label, required, hint, disabled, options, placeholder, numeric, nullable }:
  BaseProps<T> & { options: Option<string | number>[]; placeholder?: string; numeric?: boolean; nullable?: boolean }) {
  const error = errorOf(form, name);
  const setValueAs = numeric
    ? (v: unknown) => (v === "" || v === null ? null : Number(v))
    : nullable
      ? (v: unknown) => (v === "" || v === null ? null : v)
      : undefined;
  return (
    <FormField label={label} htmlFor={fieldId(name)} required={required} hint={hint} error={error}>
      <Select id={fieldId(name)} options={options} placeholder={placeholder} invalid={!!error} disabled={disabled}
        {...form.register(name, setValueAs ? { setValueAs } : undefined)} />
    </FormField>
  );
}

export function CheckboxField<T extends FieldValues>({ form, name, label, disabled }: Omit<BaseProps<T>, "required" | "hint">) {
  return <Checkbox label={label} disabled={disabled} {...form.register(name)} />;
}

/** Turns a list of strings into select options. */
export const toOptions = (values: string[]): Option<string>[] => values.map((v) => ({ value: v, label: v }));
