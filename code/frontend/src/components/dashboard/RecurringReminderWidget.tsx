import { Bell, ChevronRight, Info } from 'lucide-react'
import { useEffect, useState, useMemo } from 'react'
import { Link } from 'react-router-dom'

import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Skeleton } from '@/components/ui/skeleton'
import { recurringTransactionService } from '@/services/api'
import { RecurringTransaction } from '@/types'
import { calculateDueStatus } from '@/utils/recurring-utils'

import { UpcomingTransactionItem } from './UpcomingTransactionItem'

export function RecurringReminderWidget() {
  const [data, setData] = useState<RecurringTransaction[]>([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const fetchRecurring = async () => {
      try {
        const response = await recurringTransactionService.getAll()
        if (response.success) {
          setData(response.data)
        }
      } catch (err) {
        console.error('Failed to fetch recurring reminders', err)
      } finally {
        setLoading(false)
      }
    }
    void fetchRecurring()
  }, [])

  const reminders = useMemo(() => {
    return data
      .filter(rt => rt.isActive)
      .map(rt => ({ ...rt, status: calculateDueStatus(rt.nextRunDate) }))
      .filter(rt => rt.status === 'overdue' || rt.status === 'due-soon')
      .sort((a, b) => new Date(a.nextRunDate).getTime() - new Date(b.nextRunDate).getTime())
      .slice(0, 3)
  }, [data])

  if (loading) {
    return (
      <Card className="h-full">
        <CardHeader className="pb-3">
          <Skeleton className="h-5 w-32 mb-2" />
          <Skeleton className="h-4 w-48" />
        </CardHeader>
        <CardContent className="space-y-3">
          <Skeleton className="h-16 w-full" />
          <Skeleton className="h-16 w-full" />
        </CardContent>
      </Card>
    )
  }

  return (
    <Card className="h-full flex flex-col">
      <CardHeader className="pb-3">
        <div className="flex items-center justify-between">
          <CardTitle className="text-lg font-semibold flex items-center gap-2">
            <Bell className="h-4 w-4 text-primary" />
            Reminders
          </CardTitle>
          <Link to="/recurring" className="text-xs text-muted-foreground hover:text-primary flex items-center transition-colors">
            Manage
            <ChevronRight className="h-3 w-3 ml-0.5" />
          </Link>
        </div>
        <CardDescription>
          Upcoming and overdue schedules
        </CardDescription>
      </CardHeader>
      <CardContent className="flex-1 space-y-3">
        {reminders.length === 0 ? (
          <div className="flex flex-col items-center justify-center py-6 text-center">
            <div className="bg-muted p-3 rounded-full mb-3">
              <Info className="h-5 w-5 text-muted-foreground" />
            </div>
            <p className="text-sm font-medium text-muted-foreground">All caught up!</p>
            <p className="text-xs text-muted-foreground mt-1 px-4">
              No schedules due soon or overdue.
            </p>
          </div>
        ) : (
          reminders.map((rt) => (
            <UpcomingTransactionItem key={rt.id} transaction={rt} />
          ))
        )}
      </CardContent>
      {reminders.length > 0 && (
        <div className="px-6 pb-4 pt-0">
          <p className="text-[10px] text-muted-foreground italic">
            * Transactions generate automatically on the due date.
          </p>
        </div>
      )}
    </Card>
  )
}
