"use client";

import { useEffect, useId, useRef, useState, type KeyboardEvent, type ReactNode, type Ref } from "react";
import { Popover } from "../Popover";
import { Spinner } from "../Spinner";
import styles from "./Combobox.module.css";

export interface ComboboxItem<T> {
  key: string | number;
  value: T;
  primary: string;
  secondary?: ReactNode;
}

interface ComboboxProps<T> {
  /** Text shown in the input (query while typing, selected label otherwise). */
  query: string;
  onQueryChange: (q: string) => void;
  items: ComboboxItem<T>[];
  loading?: boolean;
  onSelect: (value: T) => void;
  /** Enter pressed while no suggestion is highlighted (e.g. barcode scan). */
  onEnterWithoutSelection?: (q: string) => void;
  placeholder?: string;
  invalid?: boolean;
  disabled?: boolean;
  inputRef?: Ref<HTMLInputElement>;
  ariaLabel: string;
  id?: string;
}

/** Search-as-you-type autocomplete (ARIA combobox) with a floating, animated suggestion panel. */
export function Combobox<T>({
  query,
  onQueryChange,
  items,
  loading,
  onSelect,
  onEnterWithoutSelection,
  placeholder,
  invalid,
  disabled,
  inputRef,
  ariaLabel,
  id,
}: ComboboxProps<T>) {
  const listId = useId();
  const [open, setOpen] = useState(false);
  const [active, setActive] = useState(-1);
  const wrapper = useRef<HTMLDivElement>(null);

  useEffect(() => setActive(-1), [items]);

  const choose = (item: ComboboxItem<T>) => {
    onSelect(item.value);
    setOpen(false);
  };

  const onKeyDown = (e: KeyboardEvent<HTMLInputElement>) => {
    if (e.key === "ArrowDown") {
      e.preventDefault();
      setOpen(true);
      setActive((a) => Math.min(items.length - 1, a + 1));
    } else if (e.key === "ArrowUp") {
      e.preventDefault();
      setActive((a) => Math.max(0, a - 1));
    } else if (e.key === "Enter") {
      e.preventDefault();
      const item = items[active];
      if (open && item) choose(item);
      else if (onEnterWithoutSelection && query.trim()) {
        onEnterWithoutSelection(query.trim());
        setOpen(false);
      } else if (items.length === 1 && items[0]) choose(items[0]);
    } else if (e.key === "Escape" || e.key === "Tab") {
      setOpen(false);
    }
  };

  const showPanel = open && (loading || items.length > 0);

  return (
    <div className={[styles.wrapper, open && styles.focused].filter(Boolean).join(" ")} ref={wrapper}>
      <span className={styles.icon} aria-hidden />
      <input
        id={id}
        ref={inputRef}
        className={invalid ? `${styles.input} ${styles.invalid}` : styles.input}
        role="combobox"
        aria-label={ariaLabel}
        aria-expanded={showPanel}
        aria-controls={listId}
        aria-autocomplete="list"
        aria-activedescendant={active >= 0 ? `${listId}-${active}` : undefined}
        autoComplete="off"
        placeholder={placeholder}
        disabled={disabled}
        value={query}
        onChange={(e) => {
          onQueryChange(e.target.value);
          setOpen(true);
        }}
        onFocus={(e) => {
          e.target.select();
          if (query) setOpen(true);
        }}
        onBlur={() => setOpen(false)}
        onKeyDown={onKeyDown}
      />
      {loading && <span className={styles.loader}><Spinner size="sm" /></span>}
      <Popover anchor={wrapper.current} open={showPanel} onClose={() => setOpen(false)} minWidth={380}>
        <ul className={styles.list} id={listId} role="listbox" aria-label={ariaLabel}>
          {loading && items.length === 0 && (
            <li className={styles.loading}>
              <span className={styles.scan} aria-hidden /> Recherche…
            </li>
          )}
          {items.map((item, i) => (
            <li
              key={item.key}
              id={`${listId}-${i}`}
              role="option"
              aria-selected={i === active}
              className={i === active ? `${styles.option} ${styles.active}` : styles.option}
              style={{ "--i": i } as React.CSSProperties}
              onMouseDown={(e) => {
                e.preventDefault();
                choose(item);
              }}
              onMouseEnter={() => setActive(i)}
            >
              <span className={styles.primary}>{item.primary}</span>
              {item.secondary && <span className={styles.secondary}>{item.secondary}</span>}
            </li>
          ))}
        </ul>
      </Popover>
    </div>
  );
}
