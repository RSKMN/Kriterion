import { AlertTriangle, CheckCircle2, XCircle } from 'lucide-react'
import React, { useEffect, useState } from 'react'

import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card'
import { Skeleton } from '@/components/ui/skeleton'
import { budgetService } from '@/services/api'
import { BudgetStatus } from '@/types'
import { cn } from '@/utils/cn'

import { BudgetProgressBar } from '../budget/BudgetProgressBar'

const currencyFormatter = new Intl.NumberFormat('en-US', {
  style: 'currency',
  currency: 'USD',
})

export function BudgetHealthCard() {
  const [status, setStatus] = useState<BudgetStatus | null>(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    let mounted = true
    const fetchStatus = async () => {
      try {
        const response = await budgetService.getStatus()
        if (response.success && mounted) {
          setStatus(response.data)
        }
      } catch (error) {
        console.error('Failed to fetch budget status', error)
      } finally {
        if (mounted) setLoading(false)
      }
    }

    void fetchStatus()
    return () => { mounted = false }
  }, [])

  if (loading) return <Skeleton className="h-[240px] w-full" />

  if (!status || status.budgetsCount === 0) return null

  const isExceeded = status.totalPercentage >= 100
  const isWarning = status.totalPercentage >= 80 && !isExceeded
  
  let healthMessage = "Overall monthly spending is within budget."
  let HealthIcon = CheckCircle2
  let iconColor = "text-emerald-500"
  let bgColor = "bg-emerald-500/10"

  if (isExceeded) {
    healthMessage = `Overall budget exceeded by ${currencyFormatter.format(Math.abs(status.totalRemaining))}.`
    HealthIcon = XCircle
    iconColor = "text-rose-500"
    bgColor = "bg-rose-500/10"
  } else if (isWarning) {
    healthMessage = "Overall spending is approaching your monthly limit."
    HealthIcon = AlertTriangle
    iconColor = "text-amber-500"
    bgColor = "bg-amber-500/10"
  }

  return (
    <Card className="h-full">
      <CardHeader>
        <CardTitle>Budget Health</CardTitle>
        <CardDescription>Real-time status of your financial limits.</CardDescription>
      </CardHeader>
      <CardContent className="space-y-6">
        <div className={cn("flex items-center gap-3 p-4 rounded-lg", bgColor)}>
          <HealthIcon className={cn("h-5 w-5 shrink-0", iconColor)} />
          <p className={cn("text-sm font-medium", iconColor)}>{healthMessage}</p>
        </div>

        <div className="space-y-2">
          <div className="flex justify-between text-sm font-medium">
            <span>Total Monthly Limit</span>
            <span>{status.totalPercentage.toFixed(1)}%</span>
          </div>
          <BudgetProgressBar percentage={status.totalPercentage} />
          <div className="flex justify-between text-xs text-muted-foreground">
            <span>{currencyFormatter.format(status.totalSpent)} spent</span>
            <span>{currencyFormatter.format(status.totalBudgetLimit)} limit</span>
          </div>
        </div>

        <div className="grid grid-cols-2 gap-4 pt-2">
          <div className="space-y-1">
            <p className="text-xs text-muted-foreground font-medium uppercase tracking-wider">Warnings</p>
            <p className={cn("text-2xl font-bold", status.warningBudgetsCount > 0 ? "text-amber-600" : "text-muted-foreground")}>
              {status.warningBudgetsCount}
            </p>
          </div>
          <div className="space-y-1">
            <p className="text-xs text-muted-foreground font-medium uppercase tracking-wider">Exceeded</p>
            <p className={cn("text-2xl font-bold", status.exceededBudgetsCount > 0 ? "text-rose-600" : "text-muted-foreground")}>
              {status.exceededBudgetsCount}
            </p>
          </div>
        </div>
      </CardContent>
    </Card>
  )
}
