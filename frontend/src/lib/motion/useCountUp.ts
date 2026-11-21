"use client";

import { useEffect, useRef, useState } from "react";
import { Spring } from "./spring";
import { prefersReducedMotion } from "./useReducedMotion";

/** Animates a number toward its new value with a soft spring (KPIs, totals). */
export function useCountUp(target: number): number {
  const [value, setValue] = useState(0);
  const spring = useRef<Spring | null>(null);

  useEffect(() => {
    if (prefersReducedMotion()) {
      setValue(target);
      return;
    }
    if (!spring.current) spring.current = new Spring(0, setValue, { stiffness: 60, damping: 16, precision: 0.5 });
    spring.current.set(target);
  }, [target]);

  useEffect(() => () => spring.current?.stop(), []);
  return value;
}
