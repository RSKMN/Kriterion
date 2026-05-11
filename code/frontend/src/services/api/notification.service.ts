import { Notification } from '@/types/notification';

import { apiClient } from './client';

export const notificationService = {
  getNotifications: async () => {
    const response = await apiClient.get<any>('/notifications');
    // The backend now returns a PageResponse inside ApiResponse.data
    return response.data.data.content as Notification[];
  },

  getUnreadCount: async () => {
    const response = await apiClient.get<any>('/notifications/unread-count');
    return response.data.data as number;
  },

  markAsRead: async (id: number) => {
    await apiClient.put(`/notifications/${id}/read`);
  },

  markAllAsRead: async () => {
    await apiClient.put('/notifications/read-all');
  },

  deleteNotification: async (id: number) => {
    await apiClient.delete(`/notifications/${id}`);
  }
};
