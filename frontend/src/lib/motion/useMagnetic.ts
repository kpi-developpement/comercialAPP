"use client";

import { useEffect, type RefObject } from "react";
import { Spring } from "./spring";
import { isCoarsePointer, prefersReducedMotion } from "./useReducedMotion";

/**
 * Magnetic hover: the element is pulled toward the cursor and springs back when it leaves.
 * @param strength maximum offset in px
 */
export function useMagnetic<T extends HTMLElement>(ref: RefObject<T | null>, strength = 6, enabled = true) {
  useEffect(() => {
    const el = ref.current;
    if (!el || !enabled || prefersReducedMotion() || isCoarsePointer()) return;
    let x = 0;
    let y = 0;
    const apply = () => el.style.setProperty("--magnet", `translate(${x.toFixed(2)}px, ${y.toFixed(2)}px)`);
    const sx = new Spring(0, (v) => { x = v; apply(); }, { stiffness: 260, damping: 18 });
    const sy = new Spring(0, (v) => { y = v; apply(); }, { stiffness: 260, damping: 18 });

    const onMove = (e: PointerEvent) => {
      const r = el.getBoundingClientRect();
      const dx = (e.clientX - (r.left + r.width / 2)) / (r.width / 2);
      const dy = (e.clientY - (r.top + r.height / 2)) / (r.height / 2);
      sx.set(dx * strength);
      sy.set(dy * strength * 0.6);
    };
    const onLeave = () => {
      sx.set(0);
      sy.set(0);
    };
    el.addEventListener("pointermove", onMove);
    el.addEventListener("pointerleave", onLeave);
    return () => {
      el.removeEventListener("pointermove", onMove);
      el.removeEventListener("pointerleave", onLeave);
      sx.stop();
      sy.stop();
    };
  }, [ref, strength, enabled]);
}
