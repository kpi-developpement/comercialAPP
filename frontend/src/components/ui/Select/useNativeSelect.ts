"use client";

import { useCallback, useRef, useState, type Ref } from "react";

const patched = new WeakSet<HTMLSelectElement>();
const valueDescriptor =
  typeof window !== "undefined" ? Object.getOwnPropertyDescriptor(HTMLSelectElement.prototype, "value") : undefined;

/**
 * Keeps a hidden native <select> as the source of truth (form libraries keep working through its ref, name,
 * onChange and onBlur) and mirrors its value into React state so the custom UI can display it — including
 * programmatic writes such as react-hook-form's reset/setValue.
 */
export function useNativeSelect(externalRef: Ref<HTMLSelectElement> | undefined) {
  const selectRef = useRef<HTMLSelectElement | null>(null);
  const [current, setCurrent] = useState("");
  const listener = useRef(setCurrent);
  listener.current = setCurrent;

  const refCallback = useCallback(
    (el: HTMLSelectElement | null) => {
      selectRef.current = el;
      if (typeof externalRef === "function") externalRef(el);
      else if (externalRef) (externalRef as { current: HTMLSelectElement | null }).current = el;
      if (!el) return;
      if (!patched.has(el) && valueDescriptor?.get && valueDescriptor.set) {
        const { get, set } = valueDescriptor;
        Object.defineProperty(el, "value", {
          configurable: true,
          get() {
            return get.call(this);
          },
          set(v: unknown) {
            set.call(this, v);
            listener.current(String(get.call(this)));
          },
        });
        patched.add(el);
      }
      setCurrent(el.value);
    },
    [externalRef],
  );

  /** Selects a value as if the user had picked it in the native control (fires React / RHF onChange). */
  const choose = useCallback((value: string) => {
    const el = selectRef.current;
    if (!el) return;
    el.value = value;
    el.dispatchEvent(new Event("change", { bubbles: true }));
    setCurrent(el.value);
  }, []);

  const blur = useCallback(() => {
    selectRef.current?.dispatchEvent(new FocusEvent("focusout", { bubbles: true }));
  }, []);

  const resync = useCallback(() => {
    if (selectRef.current) setCurrent(selectRef.current.value);
  }, []);

  return { refCallback, current, choose, blur, resync };
}
