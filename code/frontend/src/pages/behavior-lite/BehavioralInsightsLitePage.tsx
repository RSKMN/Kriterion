import React, { useEffect, useState } from 'react'
import { RefreshCw, Sparkles } from 'lucide-react'

import { behaviorLiteService } from '@/services/api/behavior-lite.service'
import { BehaviorLiteSummary, PredictLiteForecast } from '@/types/behavior-lite'
import { Button } from '@/components/ui/button'
import { Card, CardContent } from '@/components/ui/card'
import { Skeleton } from '@/components/ui/skeleton'
import { Badge } from '@/components/ui/badge'

import { SpendingVolatilityCardLite } from '@/features/behavioral-insights-lite/components/SpendingVolatilityCardLite'
import { FinancialRhythmCardLite } from '@/features/behavioral-insights-lite/components/FinancialRhythmCardLite'
import { SubscriptionPressureCardLite } from '@/features/behavioral-insights-lite/components/SubscriptionPressureCardLite'
import { SpendingBurstCardLite } from '@/features/behavioral-insights-lite/components/SpendingBurstCardLite'
import { SavingsTrendCardLite } from '@/features/behavioral-insights-lite/components/SavingsTrendCardLite'
import { BehavioralReflectionsLite } from '@/features/behavioral-insights-lite/components/BehavioralReflectionsLite'

const defaultSummary: BehaviorLiteSummary = {
  volatility: 0,
  lateNightRatio: 0,
  subscriptionPressure: 0,
  savingsTrend: 0,
  savingsTrendLabel: 'stable',
  hourlyDistribution: {},
  weeklySpending: [],
  savingsSeries: [],
  bursts: [],
  insights: [],
  transactionCount: 0,
  incomeCount: 0,
  expenseCount: 0,
  dataStatus: 'insufficient_data',
  generatedAt: new Date().toISOString(),
  fallback: true,
}

const defaultForecast: PredictLiteForecast = {
  riskLevel: 'unknown',
  trendDirection: 'stable',
  confidence: 0,
  narrative: 'Behavioral forecasts become more informative as recurring transactions accumulate.',
  signals: [],
  dataStatus: 'insufficient_data',
  fallback: true,
}

export default function BehavioralInsightsLitePage() {
  const [loading, setLoading] = useState(true)
  const [seedLoading, setSeedLoading] = useState(false)
  const [summary, setSummary] = useState<BehaviorLiteSummary>(defaultSummary)
  const [forecast, setForecast] = useState<PredictLiteForecast>(defaultForecast)

  const loadData = async () => {
    setLoading(true)
    const [summaryResult, forecastResult] = await Promise.allSettled([
      behaviorLiteService.getSummary(),
      behaviorLiteService.getForecast(),
    ])

    setSummary(summaryResult.status === 'fulfilled' && summaryResult.value ? summaryResult.value : defaultSummary)
    setForecast(forecastResult.status === 'fulfilled' && forecastResult.value ? forecastResult.value : defaultForecast)
    setLoading(false)
  }

  const seedDemoData = async () => {
    setSeedLoading(true)
    try {
      await behaviorLiteService.seedDemoData()
      await loadData()
    } catch (error) {
      console.error('Lite seeding failed', error)
    } finally {
      setSeedLoading(false)
    }
  }

  useEffect(() => {
    void loadData()
  }, [])

  if (loading) {
    return (
      <div className="space-y-5">
        <div className="flex items-center justify-between">
          <Skeleton className="h-10 w-64" />
          <Skeleton className="h-10 w-28" />
        </div>
        <div className="grid gap-4 lg:grid-cols-2">
          <Skeleton className="h-56 rounded-2xl" />
          <Skeleton className="h-56 rounded-2xl" />
          <Skeleton className="h-56 rounded-2xl" />
          <Skeleton className="h-56 rounded-2xl" />
        </div>
      </div>
    )
  }

  return (
    <div className="space-y-6 pb-12">
      <header className="flex flex-col gap-4 rounded-3xl border border-zinc-200/70 bg-gradient-to-br from-white via-white to-zinc-50 p-6 shadow-sm backdrop-blur dark:border-zinc-800 dark:from-zinc-950 dark:via-zinc-950 dark:to-zinc-900">
        <div className="flex flex-col gap-3 md:flex-row md:items-start md:justify-between">
          <div className="space-y-2">
            <div className="flex items-center gap-2 text-xs font-semibold uppercase tracking-[0.28em] text-zinc-400">
              <Sparkles className="h-4 w-4 text-indigo-500" />
              Behavioral Insights Lite
            </div>
            <h1 className="text-3xl font-semibold tracking-tight text-zinc-900 dark:text-zinc-50">Lightweight behavioral-finance analytics</h1>
            <p className="max-w-2xl text-sm leading-relaxed text-zinc-500">
              A stable, demo-ready module for spending volatility, financial rhythm, subscription pressure, burst activity, savings trends, and simple reflections.
            </p>
          </div>
          <div className="flex flex-wrap items-center gap-2">
            <Button variant="outline" className="gap-2" onClick={loadData}>
              <RefreshCw className="h-4 w-4" />
              Refresh
            </Button>
            <Button className="gap-2" onClick={seedDemoData} disabled={seedLoading}>
              <Sparkles className="h-4 w-4" />
              {seedLoading ? 'Seeding...' : 'Seed Demo Data'}
            </Button>
          </div>
        </div>

        <div className="flex flex-wrap gap-2">
          <Badge variant="outline">{summary.transactionCount} transactions</Badge>
          <Badge variant="outline">{summary.dataStatus.replace(/_/g, ' ')}</Badge>
          <Badge variant={forecast.riskLevel === 'high' ? 'destructive' : 'secondary'}>risk: {forecast.riskLevel}</Badge>
          <Badge variant="outline">trend: {forecast.trendDirection}</Badge>
          <Badge variant="outline">confidence: {Math.round(forecast.confidence * 100)}%</Badge>
        </div>

        <Card className="border-none bg-white/80 shadow-none dark:bg-zinc-950/40">
          <CardContent className="p-4 text-sm leading-relaxed text-zinc-600 dark:text-zinc-400">
            {forecast.narrative}
          </CardContent>
        </Card>
      </header>

      {summary.dataStatus !== 'ready' && (
        <Card className="border-dashed border-zinc-200 bg-zinc-50/70 dark:border-zinc-800 dark:bg-zinc-900/40">
          <CardContent className="p-5 text-sm text-zinc-500">
            {summary.dataStatus === 'insufficient_data'
              ? 'Behavioral insights improve as more transaction activity is analyzed.'
              : 'The lite module can render safely with partial data, but more history will make the signals more readable.'}
          </CardContent>
        </Card>
      )}

      <div className="grid gap-4 lg:grid-cols-2">
        <SpendingVolatilityCardLite summary={summary} />
        <FinancialRhythmCardLite summary={summary} />
        <SubscriptionPressureCardLite summary={summary} />
        <SpendingBurstCardLite summary={summary} />
      </div>

      <div className="grid gap-4 lg:grid-cols-2">
        <SavingsTrendCardLite summary={summary} />
        <Card className="border-none bg-white/70 shadow-sm backdrop-blur-sm dark:bg-zinc-900/60">
          <CardContent className="p-5">
            <BehavioralReflectionsLite insights={summary.insights} fallbackMessage="Behavioral insights improve as more financial activity is analyzed." />
          </CardContent>
        </Card>
      </div>
    </div>
  )
}
