import { create } from 'zustand';

import { notificationService } from '@/services/api/notification.service';
import { Notification } from '@/types/notification';
import { browserNotification } from '@/utils/browserNotification';

interface NotificationsState {
  notifications: Notification[];
  unreadCount: number;
  isLoading: boolean;
  fetchNotifications: (silent?: boolean) => Promise<void>;
  fetchUnreadCount: () => Promise<void>;
  markAsRead: (id: number) => Promise<void>;
  markAllAsRead: () => Promise<void>;
  deleteNotification: (id: number) => Promise<void>;
}

export const useNotificationsStore = create<NotificationsState>((set, get) => ({
  notifications: [],
  unreadCount: 0,
  isLoading: false,

  fetchNotifications: async (silent = false) => {
    if (!silent) set({ isLoading: true });
    try {
      const prevNotifications = get().notifications;
      const notifications = await notificationService.getNotifications();
      
      // If silent (e.g. from polling), check for new unread notifications to show browser alert
      if (silent && notifications.length > 0) {
        const newUnread = notifications.filter(n => 
          !n.isRead && !prevNotifications.some(prev => prev.id === n.id)
        );
        
        newUnread.forEach(n => {
          browserNotification.show(n.title, { body: n.message });
        });
      }

      set({ notifications, isLoading: false });
    } catch (error) {
      console.error('Failed to fetch notifications:', error);
      set({ isLoading: false });
    }
  },

  fetchUnreadCount: async () => {
    try {
      const unreadCount = await notificationService.getUnreadCount();
      set({ unreadCount });
    } catch (error) {
      console.error('Failed to fetch unread count:', error);
    }
  },

  markAsRead: async (id: number) => {
    try {
      await notificationService.markAsRead(id);
      set((state) => ({
        notifications: state.notifications.map((n) =>
          n.id === id ? { ...n, isRead: true } : n
        ),
        unreadCount: Math.max(0, state.unreadCount - 1),
      }));
    } catch (error) {
      console.error('Failed to mark notification as read:', error);
    }
  },

  markAllAsRead: async () => {
    try {
      await notificationService.markAllAsRead();
      set((state) => ({
        notifications: state.notifications.map((n) => ({ ...n, isRead: true })),
        unreadCount: 0,
      }));
    } catch (error) {
      console.error('Failed to mark all as read:', error);
    }
  },

  deleteNotification: async (id: number) => {
    try {
      await notificationService.deleteNotification(id);
      const notification = get().notifications.find(n => n.id === id);
      set((state) => ({
        notifications: state.notifications.filter((n) => n.id !== id),
        unreadCount: notification && !notification.isRead ? Math.max(0, state.unreadCount - 1) : state.unreadCount,
      }));
    } catch (error) {
      console.error('Failed to delete notification:', error);
    }
  },
}));
