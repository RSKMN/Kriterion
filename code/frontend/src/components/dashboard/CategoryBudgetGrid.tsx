import React, { useEffect, useState } from 'react'

import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card'
import { Skeleton } from '@/components/ui/skeleton'
import { budgetService } from '@/services/api'
import { Budget } from '@/types'

import { BudgetProgressBar } from '../budget/BudgetProgressBar'

const currencyFormatter = new Intl.NumberFormat('en-US', {
  style: 'currency',
  currency: 'USD',
})

export function CategoryBudgetGrid() {
  const [budgets, setBudgets] = useState<Budget[]>([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    let mounted = true
    const fetchBudgets = async () => {
      try {
        const response = await budgetService.getAll()
        if (response.success && mounted) {
          // Sort: Exceeded first, then Warnings, then others
          const sorted = [...response.data].sort((a, b) => {
            if (a.isExceeded && !b.isExceeded) return -1
            if (!a.isExceeded && b.isExceeded) return 1
            if (a.isWarning && !b.isWarning) return -1
            if (!a.isWarning && b.isWarning) return 1
            return b.percentage - a.percentage
          })
          setBudgets(sorted.filter(b => b.categoryId !== null).slice(0, 4))
        }
      } catch (error) {
        console.error('Failed to fetch budgets', error)
      } finally {
        if (mounted) setLoading(false)
      }
    }

    void fetchBudgets()
    return () => { mounted = false }
  }, [])

  if (loading) return <Skeleton className="h-[240px] w-full" />

  if (budgets.length === 0) return null

  return (
    <Card className="h-full">
      <CardHeader>
        <CardTitle>Category Budgets</CardTitle>
        <CardDescription>Most critical spending limits.</CardDescription>
      </CardHeader>
      <CardContent className="space-y-4">
        {budgets.map((budget) => (
          <div key={budget.id} className="space-y-1.5">
            <div className="flex justify-between text-xs font-medium">
              <span className="truncate pr-2">{budget.categoryName}</span>
              <span className={budget.isExceeded ? "text-rose-600" : budget.isWarning ? "text-amber-600" : ""}>
                {currencyFormatter.format(budget.currentSpent)} / {currencyFormatter.format(budget.monthlyLimit)}
              </span>
            </div>
            <BudgetProgressBar percentage={budget.percentage} />
          </div>
        ))}
      </CardContent>
    </Card>
  )
}
