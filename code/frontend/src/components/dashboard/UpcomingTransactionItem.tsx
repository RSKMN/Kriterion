import { Calendar, Clock, AlertTriangle } from 'lucide-react'

import { Badge } from '@/components/ui/badge'
import { RecurringTransaction } from '@/types'
import { cn } from '@/utils/cn'
import { calculateDueStatus, getDaysRemaining } from '@/utils/recurring-utils'

interface UpcomingTransactionItemProps {
  transaction: RecurringTransaction
}

const formatCurrency = (amount: number) => {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD',
  }).format(amount)
}

export function UpcomingTransactionItem({ transaction }: UpcomingTransactionItemProps) {
  const status = calculateDueStatus(transaction.nextRunDate)
  const days = getDaysRemaining(transaction.nextRunDate)

  return (
    <div className="flex items-center justify-between p-3 rounded-lg border bg-card/50 hover:bg-accent/50 transition-colors">
      <div className="flex items-center gap-3">
        <div className={cn(
          "p-2 rounded-full",
          status === 'overdue' ? "bg-rose-500/10 text-rose-600" : 
          status === 'due-soon' ? "bg-amber-500/10 text-amber-600" : 
          "bg-blue-500/10 text-blue-600"
        )}>
          {status === 'overdue' ? <AlertTriangle className="h-4 w-4" /> : 
           status === 'due-soon' ? <Clock className="h-4 w-4" /> : 
           <Calendar className="h-4 w-4" />}
        </div>
        <div>
          <h4 className="text-sm font-medium leading-none">{transaction.title}</h4>
          <p className="text-xs text-muted-foreground mt-1">
            {transaction.categoryName} • {transaction.recurrenceType.toLowerCase()}
          </p>
        </div>
      </div>
      
      <div className="text-right space-y-1">
        <div className="text-sm font-semibold">
          {transaction.type === 'INCOME' ? '+' : '-'} {formatCurrency(transaction.amount)}
        </div>
        <Badge 
          variant={status === 'overdue' ? 'destructive' : 'secondary'}
          className={cn(
            "text-[10px] h-4 px-1.5 font-normal",
            status === 'due-soon' && "bg-amber-100 text-amber-700 hover:bg-amber-100 border-none",
            status === 'upcoming' && "bg-blue-100 text-blue-700 hover:bg-blue-100 border-none"
          )}
        >
          {status === 'overdue' ? 'Overdue' : 
           status === 'due-soon' ? `Due in ${days}d` : 
           `Due in ${days}d`}
        </Badge>
      </div>
    </div>
  )
}
