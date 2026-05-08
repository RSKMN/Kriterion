import React from 'react';

import { cn } from '@/lib/utils';
import { useNotificationsStore } from '@/store/notifications.store';


interface NotificationBadgeProps {
  className?: string;
}

export const NotificationBadge: React.FC<NotificationBadgeProps> = ({ className }) => {
  const unreadCount = useNotificationsStore((state) => state.unreadCount);

  if (unreadCount === 0) return null;

  return (
    <span className={cn(
      "absolute -top-1 -right-1 flex h-4 w-4 items-center justify-center rounded-full bg-red-500 text-[10px] font-bold text-white ring-2 ring-white",
      className
    )}>
      {unreadCount > 9 ? '9+' : unreadCount}
    </span>
  );
};
