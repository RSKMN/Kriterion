import { ArrowRight, Wallet } from 'lucide-react'
import React, { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'

import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Skeleton } from '@/components/ui/skeleton'
import { budgetService } from '@/services/api'
import { BudgetStatus } from '@/types'

import { BudgetProgressBar } from '../budget/BudgetProgressBar'

const currencyFormatter = new Intl.NumberFormat('en-US', {
  style: 'currency',
  currency: 'USD',
})

export function BudgetSummaryWidget() {
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

  if (loading) return <Skeleton className="h-[200px] w-full" />

  if (!status || status.budgetsCount === 0) {
    return (
      <Card>
        <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardTitle className="text-sm font-medium">Budget Status</CardTitle>
          <Wallet className="h-4 w-4 text-muted-foreground" />
        </CardHeader>
        <CardContent className="space-y-4">
          <p className="text-xs text-muted-foreground">No budgets set for this month.</p>
          <Link 
            to="/budgets" 
            className="flex items-center text-xs font-medium text-primary hover:underline"
          >
            Create your first budget <ArrowRight className="ml-1 h-3 w-3" />
          </Link>
        </CardContent>
      </Card>
    )
  }

  return (
    <Card>
      <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
        <CardTitle className="text-sm font-medium">Monthly Budget</CardTitle>
        <Wallet className="h-4 w-4 text-muted-foreground" />
      </CardHeader>
      <CardContent className="space-y-4">
        <div className="flex items-baseline justify-between">
          <div className="text-2xl font-bold">{currencyFormatter.format(status.totalSpent)}</div>
          <div className="text-xs text-muted-foreground">
            of {currencyFormatter.format(status.totalBudgetLimit)}
          </div>
        </div>

        <BudgetProgressBar percentage={status.totalPercentage} />

        <div className="flex flex-wrap gap-2 pt-1">
          {status.warningBudgetsCount > 0 && (
            <span className="text-[10px] font-medium text-amber-600 bg-amber-50 px-1.5 py-0.5 rounded border border-amber-100">
              {status.warningBudgetsCount} Warning
            </span>
          )}
          {status.exceededBudgetsCount > 0 && (
            <span className="text-[10px] font-medium text-rose-600 bg-rose-50 px-1.5 py-0.5 rounded border border-rose-100">
              {status.exceededBudgetsCount} Exceeded
            </span>
          )}
        </div>

        <Link 
          to="/budgets" 
          className="flex items-center text-xs font-medium text-primary hover:underline"
        >
          Manage budgets <ArrowRight className="ml-1 h-3 w-3" />
        </Link>
      </CardContent>
    </Card>
  )
}
