"use client";

import { useLayoutEffect, useRef } from "react";
import { Spring } from "@/lib/motion/spring";
import styles from "./Tabs.module.css";

interface TabsProps<K extends string> {
  items: { key: K; label: string }[];
  active: K;
  onChange: (key: K) => void;
}

/**
 * Tabs with a liquid indicator: its left edge and width are two springs with different stiffness,
 * so it stretches toward the new tab then snaps (shape-shifting pill).
 */
export function Tabs<K extends string>({ items, active, onChange }: TabsProps<K>) {
  const listRef = useRef<HTMLDivElement>(null);
  const indicatorRef = useRef<HTMLSpanElement>(null);
  const springs = useRef<{ left: Spring; right: Spring } | null>(null);

  useLayoutEffect(() => {
    const el = indicatorRef.current;
    if (!el) return;
    let left = 0;
    let right = 0;
    const apply = () => {
      el.style.transform = `translateX(${left}px)`;
      el.style.width = `${Math.max(0, right - left)}px`;
    };
    springs.current = {
      left: new Spring(0, (v) => { left = v; apply(); }, { stiffness: 260, damping: 26 }),
      right: new Spring(0, (v) => { right = v; apply(); }, { stiffness: 180, damping: 22 }),
    };
    return () => {
      springs.current?.left.stop();
      springs.current?.right.stop();
    };
  }, []);

  useLayoutEffect(() => {
    const list = listRef.current;
    const tab = list?.querySelector<HTMLElement>('[aria-selected="true"]');
    const s = springs.current;
    if (!list || !tab || !s) return;
    const left = tab.offsetLeft;
    const right = left + tab.offsetWidth;
    if (s.left.current === 0 && s.right.current === 0) {
      s.left.jump(left);
      s.right.jump(right);
    } else if (right > s.right.current) {
      // moving right: the right edge leads, the left edge follows later
      s.right.set(right);
      setTimeout(() => s.left.set(left), 60);
    } else {
      s.left.set(left);
      setTimeout(() => s.right.set(right), 60);
    }
  }, [active, items]);

  return (
    <div className={styles.tabs} role="tablist" ref={listRef}>
      {items.map((item) => (
        <button
          key={item.key}
          type="button"
          role="tab"
          aria-selected={item.key === active}
          className={item.key === active ? `${styles.tab} ${styles.active}` : styles.tab}
          onClick={() => onChange(item.key)}
        >
          {item.label}
        </button>
      ))}
      <span ref={indicatorRef} className={styles.indicator} aria-hidden />
    </div>
  );
}
