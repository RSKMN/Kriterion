import { Bell, Inbox, Settings } from 'lucide-react';
import React, { useEffect } from 'react';
import { Link } from 'react-router-dom';

import { Button } from '@/components/ui/button';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import { ScrollArea } from '@/components/ui/scroll-area';
import { useNotificationsStore } from '@/store/notifications.store';

import { NotificationBadge } from './NotificationBadge';
import { NotificationCard } from './NotificationCard';


export const NotificationDropdown: React.FC = () => {
  const { 
    notifications, 
    unreadCount, 
    fetchNotifications, 
    fetchUnreadCount,
    markAsRead, 
    markAllAsRead, 
    deleteNotification 
  } = useNotificationsStore();

  useEffect(() => {
    fetchNotifications();
    fetchUnreadCount();
    
    // Polling for new notifications to trigger browser alerts
    const interval = setInterval(() => {
      fetchUnreadCount();
      fetchNotifications(true); // Silent fetch to check for new items
    }, 30000); // 30 seconds
    
    return () => clearInterval(interval);
  }, [fetchNotifications, fetchUnreadCount]);

  return (
    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        <Button variant="ghost" size="icon" className="relative h-10 w-10 rounded-full hover:bg-slate-100">
          <Bell className="h-5 w-5 text-slate-600" />
          <NotificationBadge />
        </Button>
      </DropdownMenuTrigger>
      <DropdownMenuContent className="w-[380px] p-0" align="end">
          <div className="flex items-center justify-between p-4 border-b border-slate-100">
          <div className="flex items-center gap-2">
            <h3 className="font-bold text-slate-900">Notifications</h3>
            {unreadCount > 0 && (
              <span className="px-2 py-0.5 text-[10px] font-bold bg-blue-100 text-blue-700 rounded-full uppercase">
                {unreadCount} New
              </span>
            )}
          </div>
          <div className="flex items-center gap-1">
            <Button 
              variant="ghost" 
              size="sm" 
              className="h-8 px-2 text-xs text-blue-600 hover:text-blue-700 hover:bg-blue-50"
              onClick={() => markAllAsRead()}
            >
              Mark all as read
            </Button>
            <Link to="/notifications">
              <Button variant="ghost" size="icon" className="h-8 w-8 text-slate-400">
                <Settings className="h-4 w-4" />
              </Button>
            </Link>
          </div>
        </div>
        
        <ScrollArea className="h-[400px]">
          {!notifications || notifications.length === 0 ? (
            <div className="flex flex-col items-center justify-center p-12 text-center">
              <div className="h-12 w-12 rounded-full bg-slate-50 flex items-center justify-center mb-4">
                <Inbox className="h-6 w-6 text-slate-300" />
              </div>
              <p className="text-sm font-medium text-slate-900">All caught up!</p>
              <p className="text-xs text-slate-500 mt-1">No new notifications at the moment.</p>
            </div>
          ) : (
            <div className="p-2 flex flex-col gap-1">
              {notifications.slice(0, 5).map((notification) => (
                <NotificationCard
                  key={notification.id}
                  notification={notification}
                  onMarkAsRead={markAsRead}
                  onDelete={deleteNotification}
                  variant="compact"
                />
              ))}
            </div>
          )}
        </ScrollArea>
        
        <div className="p-3 border-t border-slate-100 text-center">
          <Link to="/notifications">
            <Button variant="ghost" size="sm" className="w-full text-xs font-semibold text-slate-600 hover:text-blue-600">
              View all notifications
            </Button>
          </Link>
        </div>
      </DropdownMenuContent>
    </DropdownMenu>
  );
};
