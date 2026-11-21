"use client";

import { useEffect, useId, useLayoutEffect, useMemo, useRef, useState, type ComponentProps, type KeyboardEvent } from "react";
import type { Option } from "@/lib/types";
import { Popover } from "../Popover";
import { useNativeSelect } from "./useNativeSelect";
import styles from "./Select.module.css";

interface SelectProps extends Omit<ComponentProps<"select">, "children"> {
  options: Option<string | number>[];
  placeholder?: string;
  invalid?: boolean;
}

const SEARCH_THRESHOLD = 8;

/**
 * Custom animated dropdown (ARIA listbox) backed by a hidden native select, so it is a drop-in replacement:
 * value / onChange / register(...) work unchanged. Keyboard: ↑ ↓ Home End Enter Esc and type-ahead;
 * long lists get a filter field.
 */
export function Select({ options, placeholder, invalid, className, id, disabled, ref, value, onBlur, ...native }: SelectProps) {
  const listId = useId();
  const triggerRef = useRef<HTMLButtonElement>(null);
  const searchRef = useRef<HTMLInputElement>(null);
  const { refCallback, current, choose, blur, resync } = useNativeSelect(ref);
  const [open, setOpen] = useState(false);
  const [active, setActive] = useState(0);
  const [filter, setFilter] = useState("");
  const typeahead = useRef({ text: "", at: 0 });

  const all = useMemo(
    () => [...(placeholder !== undefined ? [{ value: "", label: placeholder }] : []), ...options],
    [options, placeholder],
  );
  const visible = useMemo(() => {
    const f = filter.trim().toLowerCase();
    return f ? all.filter((o) => o.label.toLowerCase().includes(f)) : all;
  }, [all, filter]);
  const selectedValue = value !== undefined ? String(value ?? "") : current;
  const selected = all.find((o) => String(o.value) === selectedValue);

  useLayoutEffect(() => resync(), [options, resync]);

  useEffect(() => {
    if (!open) return;
    setFilter("");
    const idx = all.findIndex((o) => String(o.value) === selectedValue);
    setActive(Math.max(0, idx));
    if (all.length > SEARCH_THRESHOLD) requestAnimationFrame(() => searchRef.current?.focus());
  }, [open]); // eslint-disable-line react-hooks/exhaustive-deps

  useEffect(() => {
    if (!open) return;
    document.getElementById(`${listId}-${active}`)?.scrollIntoView({ block: "nearest" });
  }, [active, open, listId]);

  const pick = (v: string | number) => {
    choose(String(v));
    setOpen(false);
    triggerRef.current?.focus();
  };

  const onKeyDown = (e: KeyboardEvent<HTMLElement>) => {
    if (disabled) return;
    if (!open) {
      if (["ArrowDown", "ArrowUp", "Enter", " "].includes(e.key)) {
        e.preventDefault();
        setOpen(true);
      }
      return;
    }
    switch (e.key) {
      case "ArrowDown":
        e.preventDefault();
        setActive((a) => Math.min(visible.length - 1, a + 1));
        break;
      case "ArrowUp":
        e.preventDefault();
        setActive((a) => Math.max(0, a - 1));
        break;
      case "Home":
        e.preventDefault();
        setActive(0);
        break;
      case "End":
        e.preventDefault();
        setActive(visible.length - 1);
        break;
      case "Enter": {
        e.preventDefault();
        const o = visible[active];
        if (o) pick(o.value);
        break;
      }
      case "Escape":
      case "Tab":
        setOpen(false);
        break;
      default:
        if (e.key.length === 1 && e.target === triggerRef.current) {
          const now = Date.now();
          const t = typeahead.current;
          t.text = now - t.at < 600 ? t.text + e.key.toLowerCase() : e.key.toLowerCase();
          t.at = now;
          const idx = visible.findIndex((o) => o.label.toLowerCase().startsWith(t.text));
          if (idx >= 0) setActive(idx);
        }
    }
  };

  return (
    <div className={[styles.wrapper, className].filter(Boolean).join(" ")}>
      <select
        {...native}
        ref={refCallback}
        value={value}
        onBlur={onBlur}
        disabled={disabled}
        className={styles.native}
        tabIndex={-1}
        aria-hidden
      >
        {all.map((o) => (
          <option key={o.value} value={o.value}>
            {o.label}
          </option>
        ))}
      </select>
      <button
        ref={triggerRef}
        id={id}
        type="button"
        className={[styles.trigger, open && styles.open, invalid && styles.invalid].filter(Boolean).join(" ")}
        aria-haspopup="listbox"
        aria-expanded={open}
        aria-controls={open ? listId : undefined}
        aria-label={native["aria-label"]}
        aria-invalid={invalid || undefined}
        aria-activedescendant={open ? `${listId}-${active}` : undefined}
        disabled={disabled}
        onClick={() => setOpen((o) => !o)}
        onKeyDown={onKeyDown}
        onBlur={() => !open && blur()}
      >
        <span className={selected && selected.value !== "" ? styles.value : styles.placeholder}>
          {selected?.label ?? placeholder ?? "—"}
        </span>
        <span className={styles.chevron} aria-hidden />
      </button>
      <Popover anchor={triggerRef.current} open={open} onClose={() => { setOpen(false); blur(); }} minWidth={180}>
        {all.length > SEARCH_THRESHOLD && (
          <input
            ref={searchRef}
            className={styles.search}
            placeholder="Filtrer…"
            aria-label="Filtrer les options"
            value={filter}
            onChange={(e) => { setFilter(e.target.value); setActive(0); }}
            onKeyDown={onKeyDown}
          />
        )}
        <ul id={listId} role="listbox" className={styles.list} aria-label={native["aria-label"]}>
          {visible.map((o, i) => {
            const isSelected = String(o.value) === selectedValue;
            return (
              <li
                key={o.value}
                id={`${listId}-${i}`}
                role="option"
                aria-selected={isSelected}
                className={[styles.option, i === active && styles.active, isSelected && styles.selected].filter(Boolean).join(" ")}
                style={{ "--i": i } as React.CSSProperties}
                onMouseEnter={() => setActive(i)}
                onClick={() => pick(o.value)}
              >
                <span className={styles.check} aria-hidden />
                <span className={o.value === "" ? styles.placeholder : undefined}>{o.label}</span>
              </li>
            );
          })}
          {visible.length === 0 && <li className={styles.empty}>Aucun résultat</li>}
        </ul>
      </Popover>
    </div>
  );
}
