import { RefreshCcw } from 'lucide-react'
import { useEffect, useState } from 'react'

import { DashboardSummaryCards } from '@/components/dashboard/DashboardSummaryCards'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { analyticsService } from '@/services/api'
import { getApiErrorMessage } from '@/services/api/error'
import type { DashboardSummary } from '@/types'

function formatMonthLabel(month: string) {
  const parsedDate = new Date(`${month}-01T00:00:00`)
  return new Intl.DateTimeFormat('en-US', { month: 'short', year: 'numeric' }).format(parsedDate)
}

function formatCurrency(value: number) {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD',
    maximumFractionDigits: 2,
  }).format(value)
}

export default function DashboardPage() {
  const [summary, setSummary] = useState<DashboardSummary | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  const loadSummary = async () => {
    setLoading(true)
    setError(null)

    try {
      const response = await analyticsService.getDashboardSummary()
      if (response.success) {
        setSummary(response.data)
      } else {
        setError(response.message)
      }
    } catch (requestError) {
      setError(getApiErrorMessage(requestError))
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    void loadSummary()
  }, [])

  return (
    <div className="space-y-8">
      <section className="space-y-2">
        <p className="text-sm font-medium uppercase tracking-[0.25em] text-muted-foreground">Overview</p>
        <h1 className="text-3xl font-semibold tracking-tight sm:text-4xl">Dashboard</h1>
        <p className="max-w-2xl text-sm text-muted-foreground sm:text-base">
          See your income, spending, and balance at a glance.
        </p>
      </section>

      {error ? (
        <Card className="border-destructive/40 bg-destructive/5">
          <CardHeader>
            <CardTitle className="text-destructive">Unable to load dashboard summary</CardTitle>
            <CardDescription>{error}</CardDescription>
          </CardHeader>
          <CardContent>
            <Button type="button" onClick={() => void loadSummary()}>
              <RefreshCcw className="mr-2 h-4 w-4" />
              Retry
            </Button>
          </CardContent>
        </Card>
      ) : (
        <DashboardSummaryCards summary={summary} loading={loading} />
      )}

      {!loading && summary?.monthlySummary?.length ? (
        <section className="space-y-4">
          <div>
            <h2 className="text-xl font-semibold tracking-tight">Monthly summary</h2>
            <p className="text-sm text-muted-foreground">A quick view of recent month-by-month performance.</p>
          </div>
          <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-3">
            {summary.monthlySummary.slice(0, 6).map((month) => (
              <Card key={month.month} className="overflow-hidden">
                <CardHeader className="space-y-2">
                  <CardDescription className="text-sm font-medium uppercase tracking-[0.18em]">
                    {formatMonthLabel(month.month)}
                  </CardDescription>
                  <CardTitle className="text-2xl tabular-nums">{formatCurrency(month.remainingBalance)}</CardTitle>
                </CardHeader>
                <CardContent className="space-y-2 text-sm text-muted-foreground">
                  <div className="flex items-center justify-between gap-3">
                    <span>Income</span>
                    <span className="font-medium text-foreground">{formatCurrency(month.totalIncome)}</span>
                  </div>
                  <div className="flex items-center justify-between gap-3">
                    <span>Expense</span>
                    <span className="font-medium text-foreground">{formatCurrency(month.totalExpense)}</span>
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>
        </section>
      ) : null}
    </div>
  )
}
