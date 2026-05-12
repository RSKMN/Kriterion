import React from 'react'

import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { BehaviorLiteSummary } from '@/types/behavior-lite'

interface SpendingBurstCardLiteProps {
  summary: BehaviorLiteSummary
}

export function SpendingBurstCardLite({ summary }: SpendingBurstCardLiteProps) {
  const bursts = summary.bursts ?? []

  return (
    <Card className="border-none bg-white/70 shadow-sm backdrop-blur-sm dark:bg-zinc-900/60">
      <CardHeader className="pb-3">
        <CardTitle className="text-base font-semibold text-zinc-900 dark:text-zinc-50">Spending Bursts</CardTitle>
        <CardDescription>Transactions clustered within short windows.</CardDescription>
      </CardHeader>
      <CardContent className="space-y-3">
        {bursts.length > 0 ? (
          bursts.slice(0, 4).map((burst) => (
            <div key={`${burst.label}-${burst.transactionCount}`} className="rounded-lg border border-zinc-200/80 bg-zinc-50/70 p-3 dark:border-zinc-800 dark:bg-zinc-950/40">
              <div className="flex items-center justify-between gap-3">
                <div>
                  <div className="font-medium text-zinc-900 dark:text-zinc-50">{burst.label}</div>
                  <div className="text-xs text-zinc-500">{burst.transactionCount} transactions in a short window</div>
                </div>
                <div className="text-sm text-zinc-500">{burst.totalAmount.toFixed(0)}</div>
              </div>
            </div>
          ))
        ) : (
          <p className="rounded-lg border border-dashed border-zinc-200 bg-zinc-50/70 p-4 text-sm text-zinc-500 dark:border-zinc-800 dark:bg-zinc-950/30">
            Burst activity is not yet strong enough to summarize.
          </p>
        )}
      </CardContent>
    </Card>
  )
}
