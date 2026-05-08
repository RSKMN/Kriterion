import { Bell, Inbox, CheckCircle } from 'lucide-react';
import React, { useEffect } from 'react';

import { NotificationCard } from '@/components/notifications/NotificationCard';
import { Button } from '@/components/ui/button';
import { 
  Card, 
  CardContent 
} from '@/components/ui/card';
import { Skeleton } from '@/components/ui/skeleton';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { useNotificationsStore } from '@/store/notifications.store';
import { browserNotification } from '@/utils/browserNotification';

const NotificationsPage: React.FC = () => {
  const { 
    notifications, 
    unreadCount, 
    isLoading,
    fetchNotifications, 
    markAsRead, 
    markAllAsRead, 
    deleteNotification 
  } = useNotificationsStore();

  const [permission, setPermission] = React.useState(browserNotification.getPermission());

  useEffect(() => {
    fetchNotifications();
  }, [fetchNotifications]);

  const handleRequestPermission = async () => {
    const result = await browserNotification.requestPermission();
    setPermission(result);
  };

  const unreadNotifications = notifications.filter(n => !n.isRead);
  const readNotifications = notifications.filter(n => n.isRead);

  return (
    <div className="container max-w-4xl mx-auto py-8 px-4">
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 mb-8">
        <div>
          <h1 className="text-3xl font-extrabold tracking-tight text-slate-900">Notifications</h1>
          <p className="text-slate-500 mt-1">Manage your budget alerts, payment reminders, and security updates.</p>
        </div>
        <div className="flex items-center gap-2">
          {permission !== 'granted' && (
            <Button 
              variant="outline" 
              onClick={handleRequestPermission}
              className="border-slate-200 text-slate-600 hover:bg-slate-50"
            >
              <Bell className="h-4 w-4 mr-2" />
              Enable Browser Alerts
            </Button>
          )}
          {unreadCount > 0 && (
            <Button 
              variant="outline" 
              onClick={() => markAllAsRead()}
              className="border-blue-200 text-blue-600 hover:bg-blue-50"
            >
              <CheckCircle className="h-4 w-4 mr-2" />
              Mark all as read
            </Button>
          )}
        </div>
      </div>

      <Tabs defaultValue="all" className="w-full">
        <div className="flex items-center justify-between mb-6">
          <TabsList className="bg-slate-100/50 p-1">
            <TabsTrigger value="all" className="px-6">All</TabsTrigger>
            <TabsTrigger value="unread" className="px-6 relative">
              Unread
              {unreadCount > 0 && (
                <span className="ml-2 px-1.5 py-0.5 bg-blue-500 text-white text-[10px] rounded-full">
                  {unreadCount}
                </span>
              )}
            </TabsTrigger>
            <TabsTrigger value="read" className="px-6">Read</TabsTrigger>
          </TabsList>
        </div>

        <TabsContent value="all" className="mt-0">
          <NotificationList 
            notifications={notifications} 
            isLoading={isLoading}
            onMarkAsRead={markAsRead}
            onDelete={deleteNotification}
          />
        </TabsContent>
        <TabsContent value="unread" className="mt-0">
          <NotificationList 
            notifications={unreadNotifications} 
            isLoading={isLoading}
            onMarkAsRead={markAsRead}
            onDelete={deleteNotification}
            emptyMessage="No unread notifications."
          />
        </TabsContent>
        <TabsContent value="read" className="mt-0">
          <NotificationList 
            notifications={readNotifications} 
            isLoading={isLoading}
            onMarkAsRead={markAsRead}
            onDelete={deleteNotification}
            emptyMessage="No read notifications yet."
          />
        </TabsContent>
      </Tabs>
    </div>
  );
};

interface NotificationListProps {
  notifications: any[];
  isLoading: boolean;
  onMarkAsRead: (id: number) => void;
  onDelete: (id: number) => void;
  emptyMessage?: string;
}

const NotificationList: React.FC<NotificationListProps> = ({ 
  notifications, 
  isLoading, 
  onMarkAsRead, 
  onDelete,
  emptyMessage = "No notifications found."
}) => {
  if (isLoading) {
    return (
      <div className="space-y-4">
        {[1, 2, 3].map((i) => (
          <Card key={i} className="border-slate-100">
            <CardContent className="p-4 flex gap-4">
              <Skeleton className="h-10 w-10 rounded-full" />
              <div className="space-y-2 flex-1">
                <Skeleton className="h-4 w-[200px]" />
                <Skeleton className="h-4 w-full" />
              </div>
            </CardContent>
          </Card>
        ))}
      </div>
    );
  }

  if (notifications.length === 0) {
    return (
      <Card className="border-dashed border-2 bg-slate-50/30">
        <CardContent className="flex flex-col items-center justify-center py-20">
          <div className="h-16 w-16 rounded-full bg-white shadow-sm flex items-center justify-center mb-4">
            <Inbox className="h-8 w-8 text-slate-300" />
          </div>
          <p className="text-slate-900 font-semibold">{emptyMessage}</p>
          <p className="text-slate-500 text-sm mt-1">We'll let you know when something important happens.</p>
        </CardContent>
      </Card>
    );
  }

  return (
    <div className="space-y-3">
      {notifications.map((notification) => (
        <NotificationCard
          key={notification.id}
          notification={notification}
          onMarkAsRead={onMarkAsRead}
          onDelete={onDelete}
        />
      ))}
    </div>
  );
};

export default NotificationsPage;
