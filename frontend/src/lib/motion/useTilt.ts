"use client";

import { useEffect, type RefObject } from "react";
import { Spring } from "./spring";
import { isCoarsePointer, prefersReducedMotion } from "./useReducedMotion";

/**
 * 3D tilt following the pointer with spring physics, plus a light spot (--mx / --my in %) for glare effects.
 * @param maxDeg maximum rotation in degrees
 */
export function useTilt<T extends HTMLElement>(ref: RefObject<T | null>, maxDeg = 6) {
  useEffect(() => {
    const el = ref.current;
    if (!el || prefersReducedMotion() || isCoarsePointer()) return;
    let rx = 0;
    let ry = 0;
    const apply = () => el.style.setProperty("--tilt", `perspective(900px) rotateX(${rx.toFixed(2)}deg) rotateY(${ry.toFixed(2)}deg)`);
    const sx = new Spring(0, (v) => { rx = v; apply(); }, { stiffness: 140, damping: 16 });
    const sy = new Spring(0, (v) => { ry = v; apply(); }, { stiffness: 140, damping: 16 });

    const onMove = (e: PointerEvent) => {
      const r = el.getBoundingClientRect();
      const px = (e.clientX - r.left) / r.width;
      const py = (e.clientY - r.top) / r.height;
      el.style.setProperty("--mx", `${(px * 100).toFixed(1)}%`);
      el.style.setProperty("--my", `${(py * 100).toFixed(1)}%`);
      sx.set((0.5 - py) * maxDeg * 2);
      sy.set((px - 0.5) * maxDeg * 2);
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
  }, [ref, maxDeg]);
}
