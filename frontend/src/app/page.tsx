"use client";

import { useRouter } from "next/navigation";
import { useEffect } from "react";
import { useAuth } from "@/features/auth/components/AuthProvider";
import { homeFor } from "@/lib/permissions";

/** Redirects to the role's landing page (or to login). */
export default function Home() {
  const { user, ready } = useAuth();
  const router = useRouter();
  useEffect(() => {
    if (ready) router.replace(user ? homeFor(user.role) : "/login");
  }, [ready, user, router]);
  return null;
}
