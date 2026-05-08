/**
 * Basic browser notification utility for Kriterion.
 * Handles permission requests and displaying local notifications.
 */

/* eslint-disable no-undef */
export const browserNotification = {
  /**
   * Request permission from the user to show notifications.
   */
  requestPermission: async (): Promise<NotificationPermission> => {
    if (!('Notification' in window)) {
      console.warn('This browser does not support desktop notifications');
      return 'denied';
    }

    if (Notification.permission === 'default') {
      return await Notification.requestPermission();
    }

    return Notification.permission;
  },

  /**
   * Show a notification if permission is granted.
   */
  show: (title: string, options?: NotificationOptions) => {
    if (!('Notification' in window)) return;

    if (Notification.permission === 'granted') {
      try {
        new Notification(title, {
          icon: '/favicon.ico',
          badge: '/favicon.ico',
          ...options,
        });
      } catch (error) {
        console.error('Error showing browser notification:', error);
      }
    }
  },

  /**
   * Check current permission status.
   */
  getPermission: (): NotificationPermission => {
    return 'Notification' in window ? Notification.permission : 'denied';
  }
};
