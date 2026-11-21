import type { ReactNode } from "react";
import { PageTransition } from "@/components/motion/PageTransition";

/** A template re-mounts on each navigation: every page gets the entrance transition. */
export default function AppTemplate({ children }: { children: ReactNode }) {
  return <PageTransition>{children}</PageTransition>;
}
