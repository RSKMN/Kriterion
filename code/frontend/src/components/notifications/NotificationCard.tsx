import { formatDistanceToNow } from 'date-fns';
import { 
  Bell, 
  AlertTriangle, 
  Info, 
  ShieldAlert,
  Trash2,
  Check
} from 'lucide-react';
import React from 'react';

import { Button } from '@/components/ui/button';
import { cn } from '@/lib/utils';
import { Notification } from '@/types/notification';


interface NotificationCardProps {
  notification: Notification;
  onMarkAsRead: (id: number) => void;
  onDelete: (id: number) => void;
  variant?: 'compact' | 'full';
}

export const NotificationCard: React.FC<NotificationCardProps> = ({ 
  notification, 
  onMarkAsRead, 
  onDelete
}) => {
  const getIcon = () => {
    switch (notification.type) {
      case 'BUDGET_ALERT':
        return <AlertTriangle className="h-5 w-5 text-orange-500" />;
      case 'RECURRING_REMINDER':
        return <Bell className="h-5 w-5 text-blue-500" />;
      case 'SUSPICIOUS_SPENDING':
        return <ShieldAlert className="h-5 w-5 text-red-500" />;
      case 'AI_INSIGHT':
        return <Info className="h-5 w-5 text-purple-500" />;
      default:
        return <Bell className="h-5 w-5 text-slate-500" />;
    }
  };

  const getSeverityStyles = () => {
    switch (notification.severity) {
      case 'CRITICAL':
        return 'border-l-4 border-l-red-500 bg-red-50/50';
      case 'WARNING':
        return 'border-l-4 border-l-orange-500 bg-orange-50/50';
      default:
        return 'border-l-4 border-l-blue-500 bg-blue-50/50';
    }
  };

  return (
    <div className={cn(
      "relative p-4 rounded-lg transition-all duration-200 border border-slate-200 hover:shadow-sm",
      !notification.isRead && getSeverityStyles(),
      notification.isRead && "bg-white opacity-80"
    )}>
      <div className="flex gap-4">
        <div className="flex-shrink-0 mt-1">
          {getIcon()}
        </div>
        <div className="flex-1 min-w-0">
          <div className="flex items-start justify-between gap-2">
            <h4 className={cn(
              "text-sm font-semibold text-slate-900 leading-tight truncate",
              !notification.isRead && "font-bold"
            )}>
              {notification.title}
            </h4>
            <span className="text-[10px] text-slate-400 whitespace-nowrap mt-0.5">
              {formatDistanceToNow(new Date(notification.createdAt), { addSuffix: true })}
            </span>
          </div>
          <p className="mt-1 text-sm text-slate-600 line-clamp-2 leading-relaxed">
            {notification.message}
          </p>
          
          <div className="mt-3 flex items-center gap-2">
            {!notification.isRead && (
              <Button 
                variant="ghost" 
                size="sm" 
                className="h-8 px-2 text-xs text-blue-600 hover:text-blue-700 hover:bg-blue-50"
                onClick={() => onMarkAsRead(notification.id)}
              >
                <Check className="h-3 w-3 mr-1" />
                Mark as read
              </Button>
            )}
            <Button 
              variant="ghost" 
              size="sm" 
              className="h-8 px-2 text-xs text-slate-400 hover:text-red-600 hover:bg-red-50 ml-auto"
              onClick={() => onDelete(notification.id)}
            >
              <Trash2 className="h-3 w-3" />
            </Button>
          </div>
        </div>
      </div>
    </div>
  );
};
