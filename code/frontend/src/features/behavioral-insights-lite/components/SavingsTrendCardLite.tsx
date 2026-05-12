import React from 'react'

import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { BehaviorLiteSummary } from '@/types/behavior-lite'

interface SavingsTrendCardLiteProps {
  summary: BehaviorLiteSummary
}

function buildSparkline(points: Array<{ label: string; value: number }>) {
  if (points.length === 0) {
    return ''
  }

  const values = points.map((point) => point.value)
  const min = Math.min(...values)
  const max = Math.max(...values)
  const width = 420
  const height = 120
  const padding = 12
  const range = Math.max(max - min, 1)

  return points
    .map((point, index) => {
      const x = padding + ((width - padding * 2) * index) / Math.max(points.length - 1, 1)
      const y = height - padding - ((point.value - min) / range) * (height - padding * 2)
      return `${index === 0 ? 'M' : 'L'} ${x} ${y}`
    })
    .join(' ')
}

export function SavingsTrendCardLite({ summary }: SavingsTrendCardLiteProps) {
  const points = summary.savingsSeries ?? []
  const path = buildSparkline(points)

  return (
    <Card className="border-none bg-white/70 shadow-sm backdrop-blur-sm dark:bg-zinc-900/60">
      <CardHeader className="pb-3">
        <CardTitle className="text-base font-semibold text-zinc-900 dark:text-zinc-50">Savings Trend</CardTitle>
        <CardDescription>Recent balance slope from the lite model.</CardDescription>
      </CardHeader>
      <CardContent className="space-y-4">
        <div className="flex items-center justify-between gap-3">
          <div>
            <div className="text-3xl font-semibold text-zinc-900 dark:text-zinc-50">{summary.savingsTrend.toFixed(2)}</div>
            <div className="text-xs uppercase tracking-[0.24em] text-zinc-400">{summary.savingsTrendLabel}</div>
          </div>
          <div className="text-sm text-zinc-500">30-day balance slope</div>
        </div>

        {points.length > 0 ? (
          <svg viewBox="0 0 420 120" className="h-28 w-full overflow-visible">
            <path d={path} fill="none" stroke="#10b981" strokeWidth="3" strokeLinecap="round" strokeLinejoin="round" />
          </svg>
        ) : (
          <p className="rounded-lg border border-dashed border-zinc-200 bg-zinc-50/70 p-4 text-sm text-zinc-500 dark:border-zinc-800 dark:bg-zinc-950/30">
            Savings trend requires a small run of income and expense history.
          </p>
        )}
      </CardContent>
    </Card>
  )
}
