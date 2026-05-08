import React from 'react'

import { Progress } from '@/components/ui/progress'
import { cn } from '@/utils/cn'

interface BudgetProgressBarProps {
  percentage: number
  className?: string
}

export function BudgetProgressBar({ percentage, className }: BudgetProgressBarProps) {
  // Clamp percentage for display
  const displayValue = Math.min(Math.max(percentage, 0), 100)
  
  // Determine color based on threshold
  const colorClass = percentage >= 100 
    ? 'bg-rose-500' 
    : percentage >= 80 
      ? 'bg-amber-500' 
      : 'bg-emerald-500'

  return (
    <div className={cn('space-y-1', className)}>
      <Progress 
        value={displayValue} 
        className="h-2 w-full" 
        indicatorClassName={colorClass}
      />
    </div>
  )
}
