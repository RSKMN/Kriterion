import React from 'react'

import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Progress } from '@/components/ui/progress'
import { BehaviorLiteSummary } from '@/types/behavior-lite'

interface SubscriptionPressureCardLiteProps {
  summary: BehaviorLiteSummary
}

export function SubscriptionPressureCardLite({ summary }: SubscriptionPressureCardLiteProps) {
  return (
    <Card className="border-none bg-white/70 shadow-sm backdrop-blur-sm dark:bg-zinc-900/60">
      <CardHeader className="pb-3">
        <CardTitle className="text-base font-semibold text-zinc-900 dark:text-zinc-50">Subscription Pressure</CardTitle>
        <CardDescription>Recurring expenses compared with income capacity.</CardDescription>
      </CardHeader>
      <CardContent className="space-y-4">
        <div className="flex items-end justify-between gap-3">
          <div>
            <div className="text-3xl font-semibold text-zinc-900 dark:text-zinc-50">{(summary.subscriptionPressure * 100).toFixed(0)}%</div>
            <div className="text-xs uppercase tracking-[0.24em] text-zinc-400">recurring load</div>
          </div>
          <div className="text-right text-sm text-zinc-500">
            <div>{summary.incomeCount} income events</div>
            <div>{summary.expenseCount} expense events</div>
          </div>
        </div>
        <Progress value={Math.min(summary.subscriptionPressure * 100, 100)} className="h-2 bg-zinc-200 dark:bg-zinc-800" />
        <p className="text-sm leading-relaxed text-zinc-500">
          {summary.subscriptionPressure > 0.35
            ? 'Recurring obligations are consuming a growing share of available balance.'
            : 'Recurring obligations remain visible but manageable in the current snapshot.'}
        </p>
      </CardContent>
    </Card>
  )
}
