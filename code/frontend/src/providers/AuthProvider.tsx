import type { ReactNode } from 'react'

import { useAuthInitialize } from '@/store/auth.store'

interface AuthProviderProps {
  children: ReactNode
}

export function AuthProvider({ children }: AuthProviderProps) {
  // Initialize auth state from localStorage on app load
  useAuthInitialize()

  return children
}
