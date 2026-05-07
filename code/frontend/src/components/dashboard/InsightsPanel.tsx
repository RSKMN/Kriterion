import React, { useEffect, useState } from 'react'

import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Skeleton } from '@/components/ui/skeleton'
import { analyticsService } from '@/services/api'
import type { MonthlyTrendItem } from '@/types'

export function InsightsPanel() {
  const [data, setData] = useState<MonthlyTrendItem[] | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    let mounted = true
    void (async () => {
      setLoading(true)
      setError(null)
      try {
        const res = await analyticsService.getMonthlyTrends()
        if (res.success) {
          if (mounted) setData(res.data)
        } else {
          setError(res.message)
        }
      } catch {
        setError('Unable to load trend data')
      } finally {
        if (mounted) setLoading(false)
      }
    })()
    return () => {
      mounted = false
    }
  }, [])

  if (loading) return <Skeleton className="h-40 w-full" />
  if (error)
    return (
      <Card>
        <CardHeader>
          <CardTitle>Insights</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="text-destructive">{error}</div>
        </CardContent>
      </Card>
    )

  if (!data || data.length < 2)
    return (
      <Card>
        <CardHeader>
          <CardTitle>Insights</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="text-sm text-muted-foreground">Not enough data for trend insights.</div>
        </CardContent>
      </Card>
    )

  // compare last two months
  const latest = data[0]
  const prev = data[1]
  const insights: string[] = []

  if (prev && latest) {
    if (prev.totalExpense > 0) {
      const change = ((latest.totalExpense - prev.totalExpense) / prev.totalExpense) * 100
      const rounded = Math.round(change)
      if (Math.abs(rounded) >= 1) {
        insights.push(`Overall spending ${change > 0 ? 'increased' : 'decreased'} by ${Math.abs(rounded)}% compared to last month`)
      }
    }

    // top category mention from monthly data isn't available; suggest checking top categories
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle>Insights</CardTitle>
      </CardHeader>
      <CardContent>
        {insights.length === 0 ? (
          <div className="text-sm text-muted-foreground">No notable changes this month.</div>
        ) : (
          <ul className="space-y-2">
            {insights.map((ins, i) => (
              <li key={i} className="text-sm">
                {ins}
              </li>
            ))}
          </ul>
        )}
      </CardContent>
    </Card>
  )
}
