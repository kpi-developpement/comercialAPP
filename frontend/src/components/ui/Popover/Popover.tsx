"use client";

import { useEffect, useLayoutEffect, useRef, useState, type ReactNode } from "react";
import { createPortal } from "react-dom";
import styles from "./Popover.module.css";

interface PopoverProps {
  anchor: HTMLElement | null;
  open: boolean;
  onClose: () => void;
  children: ReactNode;
  minWidth?: number;
  maxHeight?: number;
  id?: string;
  role?: string;
}

interface Position {
  left: number;
  top: number;
  width: number;
  placement: "bottom" | "top";
}

const GAP = 6;

/**
 * Floating panel rendered in a portal with fixed positioning (never clipped by scrolling tables or modals).
 * Flips above the anchor when there is not enough room below; morphs in from the anchor edge.
 */
export function Popover({ anchor, open, onClose, children, minWidth = 0, maxHeight = 320, id, role }: PopoverProps) {
  const panelRef = useRef<HTMLDivElement>(null);
  const [pos, setPos] = useState<Position | null>(null);
  const [mounted, setMounted] = useState(open);
  const [closing, setClosing] = useState(false);

  useEffect(() => {
    if (open) {
      setMounted(true);
      setClosing(false);
    } else if (mounted) {
      setClosing(true);
      const t = setTimeout(() => {
        setMounted(false);
        setClosing(false);
      }, 140);
      return () => clearTimeout(t);
    }
  }, [open, mounted]);

  useLayoutEffect(() => {
    if (!mounted || !anchor) return;
    const place = () => {
      const r = anchor.getBoundingClientRect();
      const below = window.innerHeight - r.bottom;
      const placement = below < Math.min(maxHeight, 220) && r.top > below ? "top" : "bottom";
      const width = Math.max(r.width, minWidth);
      const left = Math.min(Math.max(8, r.left), window.innerWidth - width - 8);
      setPos({ left, width, placement, top: placement === "bottom" ? r.bottom + GAP : r.top - GAP });
    };
    place();
    window.addEventListener("scroll", place, true);
    window.addEventListener("resize", place);
    return () => {
      window.removeEventListener("scroll", place, true);
      window.removeEventListener("resize", place);
    };
  }, [mounted, anchor, minWidth, maxHeight]);

  useEffect(() => {
    if (!open) return;
    const onDown = (e: PointerEvent) => {
      const t = e.target as Node;
      if (panelRef.current?.contains(t) || anchor?.contains(t)) return;
      onClose();
    };
    document.addEventListener("pointerdown", onDown);
    return () => document.removeEventListener("pointerdown", onDown);
  }, [open, anchor, onClose]);

  if (!mounted || !pos || typeof document === "undefined") return null;
  return createPortal(
    <div
      ref={panelRef}
      id={id}
      role={role}
      className={[styles.panel, styles[pos.placement], closing && styles.closing].filter(Boolean).join(" ")}
      style={{
        left: pos.left,
        width: pos.width,
        maxHeight,
        top: pos.placement === "bottom" ? pos.top : undefined,
        bottom: pos.placement === "top" ? window.innerHeight - pos.top : undefined,
      }}
      onMouseDown={(e) => {
        // Keep focus on the anchor (combobox input / select trigger), except for fields inside the panel.
        if (!(e.target instanceof HTMLInputElement)) e.preventDefault();
      }}
    >
      {children}
    </div>,
    document.body,
  );
}
