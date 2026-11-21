"use client";

import { useEffect, type RefObject } from "react";
import { prefersReducedMotion } from "./useReducedMotion";

/**
 * Scroll parallax: writes --parallax (px) = scrollY × speed on the element.
 * Negative speeds move against the scroll.
 */
export function useParallax<T extends HTMLElement>(ref: RefObject<T | null>, speed = 0.15) {
  useEffect(() => {
    const el = ref.current;
    if (!el || prefersReducedMotion()) return;
    let frame = 0;
    const update = () => {
      frame = 0;
      el.style.setProperty("--parallax", `${(window.scrollY * speed).toFixed(1)}px`);
    };
    const onScroll = () => {
      if (!frame) frame = requestAnimationFrame(update);
    };
    update();
    window.addEventListener("scroll", onScroll, { passive: true });
    return () => {
      cancelAnimationFrame(frame);
      window.removeEventListener("scroll", onScroll);
    };
  }, [ref, speed]);
}
