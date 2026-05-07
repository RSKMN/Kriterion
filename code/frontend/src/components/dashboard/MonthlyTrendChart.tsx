import React, { useEffect, useState } from 'react'
import { ResponsiveContainer, LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend } from 'recharts'

import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Skeleton } from '@/components/ui/skeleton'
import { analyticsService } from '@/services/api'
import type { MonthlyTrendItem } from '@/types'

export function MonthlyTrendChart() {
  const [data, setData] = useState<MonthlyTrendItem[] | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    let mounted = true
    void (async () => {
      setLoading(true)
      setError(null)
      try {
        const response = await analyticsService.getMonthlyTrends()
        if (response.success) {
          if (mounted) setData(response.data)
        } else {
          setError(response.message)
        }
      } catch {
        setError('Unable to load monthly trends')
      } finally {
        if (mounted) setLoading(false)
      }
    })()
    return () => {
      mounted = false
    }
  }, [])

  return (
    <Card>
      <CardHeader>
        <CardTitle>Monthly spending trend</CardTitle>
      </CardHeader>
      <CardContent>
        {loading ? (
          <Skeleton className="h-64 w-full" />
        ) : error ? (
          <div className="text-destructive">{error}</div>
        ) : !data || data.length === 0 ? (
          <div className="text-sm text-muted-foreground">No monthly trend data to display.</div>
        ) : (
          <div style={{ width: '100%', height: 300 }}>
            <ResponsiveContainer>
              <LineChart data={data} margin={{ top: 10, right: 20, left: 0, bottom: 0 }}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="month" tickFormatter={(m) => new Date(`${m}-01`).toLocaleString(undefined, { month: 'short' })} />
                <YAxis />
                <Tooltip formatter={(value: number) => new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(value)} />
                <Legend />
                <Line type="monotone" dataKey="totalExpense" stroke="#ef4444" strokeWidth={2} dot={false} />
                <Line type="monotone" dataKey="totalIncome" stroke="#10b981" strokeWidth={2} dot={false} />
              </LineChart>
            </ResponsiveContainer>
          </div>
        )}
      </CardContent>
    </Card>
  )
}
