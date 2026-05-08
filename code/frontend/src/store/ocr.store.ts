import { create } from 'zustand'
import { persist, createJSONStorage } from 'zustand/middleware'


export interface ScanHistoryItem {
  id: string
  timestamp: string
  merchantName: string
  amount: number
  date: string
  previewUrl?: string // Base64 or local blob URL (warning: blobs expire)
  status: 'completed' | 'pending' | 'failed'
}

interface OCRState {
  recentScans: ScanHistoryItem[]
  addScan: (scan: Omit<ScanHistoryItem, 'id' | 'timestamp'>) => void
  clearHistory: () => void
}

export const useOCRStore = create<OCRState>()(
  persist(
    (set) => ({
      recentScans: [],
      addScan: (scan) => {
        const newItem: ScanHistoryItem = {
          ...scan,
          id: Math.random().toString(36).substring(7),
          timestamp: new Date().toISOString(),
        }
        set((state) => ({
          recentScans: [newItem, ...state.recentScans].slice(0, 10), // Keep last 10
        }))
      },
      clearHistory: () => set({ recentScans: [] }),
    }),
    {
      name: 'kriterion-ocr-storage',
      storage: createJSONStorage(() => localStorage),
    }
  )
)
