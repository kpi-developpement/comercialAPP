"use client";

import { useEffect, type RefObject } from "react";
import { isCoarsePointer } from "./useReducedMotion";

/** Writes the pointer position into --mx / --my (px) so CSS can paint a light that follows the cursor. */
export function useSpotlight<T extends HTMLElement>(ref: RefObject<T | null>) {
  useEffect(() => {
    const el = ref.current;
    if (!el || isCoarsePointer()) return;
    let frame = 0;
    const onMove = (e: PointerEvent) => {
      cancelAnimationFrame(frame);
      frame = requestAnimationFrame(() => {
        const r = el.getBoundingClientRect();
        el.style.setProperty("--mx", `${e.clientX - r.left}px`);
        el.style.setProperty("--my", `${e.clientY - r.top}px`);
      });
    };
    el.addEventListener("pointermove", onMove);
    return () => {
      cancelAnimationFrame(frame);
      el.removeEventListener("pointermove", onMove);
    };
  }, [ref]);
}
