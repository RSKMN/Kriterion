import React, { useEffect, useState } from 'react'

import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Skeleton } from '@/components/ui/skeleton'
import { analyticsService } from '@/services/api'
import type { WeeklyInsight } from '@/types'

export function WeeklyInsights() {
  const [data, setData] = useState<WeeklyInsight[] | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    let mounted = true
    void (async () => {
      setLoading(true)
      setError(null)
      try {
        const res = await analyticsService.getWeeklyInsights()
        if (res.success) {
          if (mounted) setData(res.data)
        } else {
          setError(res.message)
        }
      } catch {
        setError('Unable to load weekly insights')
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
          <CardTitle>Weekly insights</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="text-destructive">{error}</div>
        </CardContent>
      </Card>
    )

  if (!data || data.length === 0)
    return (
      <Card>
        <CardHeader>
          <CardTitle>Weekly insights</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="text-sm text-muted-foreground">No weekly data available.</div>
        </CardContent>
      </Card>
    )

  // Simple insights: compare last two weeks
  const latest = data[0]
  const previous = data[1]
  const insights: string[] = []

  if (latest && previous) {
    if (previous.totalExpense > 0) {
      const change = ((latest.totalExpense - previous.totalExpense) / previous.totalExpense) * 100
      const rounded = Math.round(change)
      if (Math.abs(rounded) >= 1) {
        insights.push(`Spending ${change > 0 ? 'increased' : 'decreased'} by ${Math.abs(rounded)}% this week`)
      }
    }

    if (latest.totalExpense > latest.totalIncome) {
      insights.push('Expenses exceeded income this week')
    }
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle>Weekly insights</CardTitle>
      </CardHeader>
      <CardContent>
        {insights.length === 0 ? (
          <div className="text-sm text-muted-foreground">No notable weekly changes.</div>
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
