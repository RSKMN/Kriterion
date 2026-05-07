import React, { useEffect, useState } from 'react'
import { ResponsiveContainer, PieChart, Pie, Cell, Tooltip, Legend } from 'recharts'

import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Skeleton } from '@/components/ui/skeleton'
import { analyticsService } from '@/services/api'
import type { CategoryBreakdownItem } from '@/types'

const COLORS = ['#06b6d4', '#10b981', '#f97316', '#ef4444', '#6366f1', '#f59e0b']

export function PieChartCard() {
  const [data, setData] = useState<CategoryBreakdownItem[] | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    let mounted = true
    void (async () => {
      setLoading(true)
      setError(null)
      try {
        const response = await analyticsService.getCategoryBreakdown()
        if (response.success) {
          if (mounted) setData(response.data)
        } else {
          setError(response.message)
        }
      } catch {
        setError('Unable to load category breakdown')
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
        <CardTitle>Spending by category</CardTitle>
      </CardHeader>
      <CardContent>
        {loading ? (
          <Skeleton className="h-64 w-full" />
        ) : error ? (
          <div className="text-destructive">{error}</div>
        ) : !data || data.length === 0 ? (
          <div className="text-sm text-muted-foreground">No category spending to display.</div>
        ) : (
          <div style={{ width: '100%', height: 280 }}>
            <ResponsiveContainer>
              <PieChart>
                <Pie dataKey="total" data={data} nameKey="category" outerRadius={90} fill="#8884d8" label>
                  {data.map((entry, index) => (
                    <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                  ))}
                </Pie>
                <Tooltip formatter={(value: number) => new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(value)} />
                <Legend />
              </PieChart>
            </ResponsiveContainer>
          </div>
        )}
      </CardContent>
    </Card>
  )
}
