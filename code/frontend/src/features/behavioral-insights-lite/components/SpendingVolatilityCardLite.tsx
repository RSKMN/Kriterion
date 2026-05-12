import React from 'react'

import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import { BehaviorLiteSummary } from '@/types/behavior-lite'

interface SpendingVolatilityCardLiteProps {
  summary: BehaviorLiteSummary
}

export function SpendingVolatilityCardLite({ summary }: SpendingVolatilityCardLiteProps) {
  const weeklySeries = summary.weeklySpending ?? []
  const maxValue = Math.max(...weeklySeries.map((point) => point.value), 1)
  const level = summary.volatility > 1.2 ? 'elevated' : summary.volatility > 0.65 ? 'moderate' : 'stable'

  return (
    <Card className="border-none bg-white/70 shadow-sm backdrop-blur-sm dark:bg-zinc-900/60">
      <CardHeader className="pb-3">
        <CardTitle className="text-base font-semibold text-zinc-900 dark:text-zinc-50">Spending Volatility</CardTitle>
        <CardDescription>Weekly expense dispersion across the recent window.</CardDescription>
      </CardHeader>
      <CardContent className="space-y-4">
        <div className="flex items-center justify-between gap-3">
          <div>
            <div className="text-3xl font-semibold text-zinc-900 dark:text-zinc-50">{summary.volatility.toFixed(2)}</div>
            <div className="text-xs uppercase tracking-[0.24em] text-zinc-400">{level}</div>
          </div>
          <Badge variant="outline" className="capitalize">{summary.dataStatus.replace(/_/g, ' ')}</Badge>
        </div>

        {weeklySeries.length > 0 ? (
          <div className="space-y-2">
            <div className="grid grid-cols-1 gap-2 sm:grid-cols-2">
              {weeklySeries.slice(-4).map((point) => (
                <div key={point.label} className="rounded-lg border border-zinc-200/80 bg-zinc-50/70 p-3 dark:border-zinc-800 dark:bg-zinc-950/40">
                  <div className="flex items-center justify-between text-xs text-zinc-500">
                    <span>{point.label}</span>
                    <span>{point.value.toFixed(0)}</span>
                  </div>
                  <div className="mt-2 h-2 overflow-hidden rounded-full bg-zinc-200 dark:bg-zinc-800">
                    <div className="h-full rounded-full bg-indigo-500" style={{ width: `${(point.value / maxValue) * 100}%` }} />
                  </div>
                </div>
              ))}
            </div>
          </div>
        ) : (
          <p className="rounded-lg border border-dashed border-zinc-200 bg-zinc-50/70 p-4 text-sm text-zinc-500 dark:border-zinc-800 dark:bg-zinc-950/30">
            Behavioral insights improve as more transaction activity is analyzed.
          </p>
        )}
      </CardContent>
    </Card>
  )
}
