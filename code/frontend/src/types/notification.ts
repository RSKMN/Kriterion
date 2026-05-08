export type NotificationType = 'BUDGET_ALERT' | 'RECURRING_REMINDER' | 'SYSTEM' | 'AI_INSIGHT' | 'SUSPICIOUS_SPENDING';
export type NotificationSeverity = 'INFO' | 'WARNING' | 'CRITICAL';

export interface Notification {
  id: number;
  title: string;
  message: string;
  type: NotificationType;
  severity: NotificationSeverity;
  isRead: boolean;
  createdAt: string;
}

export interface NotificationResponse {
  unreadCount: number;
  notifications: Notification[];
}
