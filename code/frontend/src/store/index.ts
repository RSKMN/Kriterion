// Zustand store entrypoint. Keep stores small and feature-scoped.
import {create} from 'zustand'

type AppState = {}

export const useAppStore = create<AppState>(() => ({}))
