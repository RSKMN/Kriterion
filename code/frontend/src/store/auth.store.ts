import { useEffect } from 'react'
import { create } from 'zustand'

import type { AuthUser } from '@/types'

interface AuthState {
  user: AuthUser | null
  token: string | null
  isAuthenticated: boolean
  isLoading: boolean
  setAuth: (user: AuthUser | null, token: string | null) => void
  clearAuth: () => void
  initializeAuth: () => void
}

const STORAGE_KEY_TOKEN = 'kriterion_token'
const STORAGE_KEY_USER = 'kriterion_user'

export const useAuthStore = create<AuthState>((set) => ({
  user: null,
  token: null,
  isAuthenticated: false,
  isLoading: true,
  setAuth: (user, token) => {
    if (user && token) {
      localStorage.setItem(STORAGE_KEY_USER, JSON.stringify(user))
      localStorage.setItem(STORAGE_KEY_TOKEN, token)
      set({ user, token, isAuthenticated: true, isLoading: false })
    } else {
      localStorage.removeItem(STORAGE_KEY_USER)
      localStorage.removeItem(STORAGE_KEY_TOKEN)
      set({ user: null, token: null, isAuthenticated: false, isLoading: false })
    }
  },
  clearAuth: () => {
    localStorage.removeItem(STORAGE_KEY_USER)
    localStorage.removeItem(STORAGE_KEY_TOKEN)
    set({ user: null, token: null, isAuthenticated: false, isLoading: false })
  },
  initializeAuth: () => {
    const storedToken = localStorage.getItem(STORAGE_KEY_TOKEN)
    const storedUser = localStorage.getItem(STORAGE_KEY_USER)
    if (storedToken && storedUser) {
      try {
        const user = JSON.parse(storedUser)
        set({ user, token: storedToken, isAuthenticated: true, isLoading: false })
      } catch {
        localStorage.removeItem(STORAGE_KEY_USER)
        localStorage.removeItem(STORAGE_KEY_TOKEN)
        set({ user: null, token: null, isAuthenticated: false, isLoading: false })
      }
    } else {
      set({ user: null, token: null, isAuthenticated: false, isLoading: false })
    }
  },
}))

// Custom hook to initialize auth on app load
export function useAuthInitialize() {
  useEffect(() => {
    useAuthStore.getState().initializeAuth()
  }, [])
}
