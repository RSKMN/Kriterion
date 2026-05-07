import { create } from 'zustand'

interface NotificationItem {
  id: string
  title: string
  description?: string
}

interface NotificationsState {
  items: NotificationItem[]
  addNotification: (notification: NotificationItem) => void
  removeNotification: (id: string) => void
}

export const useNotificationsStore = create<NotificationsState>((set) => ({
  items: [],
  addNotification: (notification) => set((state) => ({ items: [...state.items, notification] })),
  removeNotification: (id) => set((state) => ({ items: state.items.filter((item) => item.id !== id) })),
}))
