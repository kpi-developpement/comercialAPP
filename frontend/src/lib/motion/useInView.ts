"use client";

import { useEffect, useState, type RefObject } from "react";

/** Becomes true (once) when the element scrolls into the viewport. */
export function useInView<T extends HTMLElement>(ref: RefObject<T | null>, rootMargin = "0px 0px -8% 0px"): boolean {
  const [inView, setInView] = useState(false);
  useEffect(() => {
    const el = ref.current;
    if (!el || inView) return;
    if (typeof IntersectionObserver === "undefined") {
      setInView(true);
      return;
    }
    const io = new IntersectionObserver(
      (entries) => {
        if (entries.some((e) => e.isIntersecting)) {
          setInView(true);
          io.disconnect();
        }
      },
      { rootMargin, threshold: 0.05 },
    );
    io.observe(el);
    return () => io.disconnect();
  }, [ref, rootMargin, inView]);
  return inView;
}
