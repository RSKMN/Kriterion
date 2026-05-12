import React from 'react'

import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { BehaviorLiteSummary } from '@/types/behavior-lite'

interface FinancialRhythmCardLiteProps {
  summary: BehaviorLiteSummary
}

export function FinancialRhythmCardLite({ summary }: FinancialRhythmCardLiteProps) {
  const hourlyEntries = Object.entries(summary.hourlyDistribution ?? {})
    .map(([hour, value]) => ({ hour: Number(hour), value }))
    .sort((left, right) => left.hour - right.hour)

  const maxValue = Math.max(...hourlyEntries.map((point) => point.value), 1)

  return (
    <Card className="border-none bg-white/70 shadow-sm backdrop-blur-sm dark:bg-zinc-900/60">
      <CardHeader className="pb-3">
        <CardTitle className="text-base font-semibold text-zinc-900 dark:text-zinc-50">Financial Rhythm</CardTitle>
        <CardDescription>Hourly transaction distribution and late-night intensity.</CardDescription>
      </CardHeader>
      <CardContent className="space-y-4">
        {hourlyEntries.some((point) => point.value > 0) ? (
          <div className="space-y-3">
            <div className="grid grid-cols-12 gap-1">
              {hourlyEntries.map((point) => (
                <div key={point.hour} className="flex h-24 flex-col justify-end">
                  <div
                    className="rounded-t-md bg-sky-500/85 transition-all"
                    style={{ height: `${Math.max((point.value / maxValue) * 100, point.value > 0 ? 6 : 2)}%` }}
                    title={`${point.hour}:00 - ${point.value.toFixed(2)}`}
                  />
                </div>
              ))}
            </div>
            <div className="flex items-center justify-between text-xs text-zinc-500">
              <span>Late-night ratio</span>
              <span>{(summary.lateNightRatio * 100).toFixed(1)}%</span>
            </div>
          </div>
        ) : (
          <p className="rounded-lg border border-dashed border-zinc-200 bg-zinc-50/70 p-4 text-sm text-zinc-500 dark:border-zinc-800 dark:bg-zinc-950/30">
            Not enough financial history yet to estimate spending rhythm.
          </p>
        )}
      </CardContent>
    </Card>
  )
}
