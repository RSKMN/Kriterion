import { Pencil, Trash2 } from 'lucide-react'
import React from 'react'

import { Badge } from '@/components/ui/badge'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Budget } from '@/types'

import { BudgetProgressBar } from './BudgetProgressBar'

interface BudgetCardProps {
  budget: Budget
  onEdit: (budget: Budget) => void
  onDelete: (id: number) => void
}

const currencyFormatter = new Intl.NumberFormat('en-US', {
  style: 'currency',
  currency: 'USD',
})

export function BudgetCard({ budget, onEdit, onDelete }: BudgetCardProps) {
  const isOver = budget.percentage >= 100
  const isNear = budget.percentage >= 80 && !isOver

  return (
    <Card className="overflow-hidden transition-all hover:shadow-md">
      <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
        <div className="space-y-1">
          <CardTitle className="text-sm font-medium">{budget.categoryName || 'Overall Budget'}</CardTitle>
          <div className="flex gap-2">
            {isOver && <Badge variant="destructive">Exceeded</Badge>}
            {isNear && <Badge variant="secondary" className="bg-amber-500/10 text-amber-600 border-amber-500/20">Warning</Badge>}
          </div>
        </div>
        <div className="flex gap-1">
          <button 
            onClick={() => onEdit(budget)}
            className="rounded-full p-2 text-muted-foreground hover:bg-muted hover:text-foreground transition-colors"
          >
            <Pencil className="h-4 w-4" />
          </button>
          <button 
            onClick={() => onDelete(budget.id)}
            className="rounded-full p-2 text-muted-foreground hover:bg-destructive/10 hover:text-destructive transition-colors"
          >
            <Trash2 className="h-4 w-4" />
          </button>
        </div>
      </CardHeader>
      <CardContent className="space-y-4">
        <div className="flex items-baseline justify-between">
          <div className="text-2xl font-bold">{currencyFormatter.format(budget.currentSpent)}</div>
          <div className="text-sm text-muted-foreground">
            of {currencyFormatter.format(budget.monthlyLimit)}
          </div>
        </div>
        
        <BudgetProgressBar percentage={budget.percentage} />
        
        <div className="flex justify-between text-xs text-muted-foreground">
          <span>{budget.percentage.toFixed(0)}% used</span>
          <span>{currencyFormatter.format(budget.remaining)} {budget.remaining >= 0 ? 'left' : 'over'}</span>
        </div>
      </CardContent>
    </Card>
  )
}
