import React, { useEffect, useState } from 'react'

import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Skeleton } from '@/components/ui/skeleton'
import { analyticsService } from '@/services/api'
import type { TopCategoryItem, CategoryBreakdownItem } from '@/types'

export function TopCategories() {
  const [data, setData] = useState<CategoryBreakdownItem[] | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    let mounted = true
    void (async () => {
      setLoading(true)
      setError(null)
      try {
        const res = await analyticsService.getCategoryBreakdown()
        if (res.success) {
          if (mounted) setData(res.data)
        } else {
          setError(res.message)
        }
      } catch {
        setError('Unable to load top categories')
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
          <CardTitle>Top categories</CardTitle>
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
          <CardTitle>Top categories</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="text-sm text-muted-foreground">No category data available.</div>
        </CardContent>
      </Card>
    )

  const total = data.reduce((s, d) => s + d.total, 0) || 1
  const top = data
    .slice()
    .sort((a, b) => b.total - a.total)
    .slice(0, 5)
    .map((d): TopCategoryItem => ({ category: d.category, total: d.total, percentage: Number(((d.total / total) * 100).toFixed(1)) }))

  return (
    <Card>
      <CardHeader>
        <CardTitle>Top categories</CardTitle>
      </CardHeader>
      <CardContent>
        <ul className="space-y-3">
          {top.map((t) => (
            <li key={t.category} className="flex items-center justify-between">
              <span className="font-medium">{t.category}</span>
              <div className="flex items-center gap-3">
                <span className="text-sm text-muted-foreground">{t.percentage}%</span>
                <span className="tabular-nums font-semibold">{new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(t.total)}</span>
              </div>
            </li>
          ))}
        </ul>
      </CardContent>
    </Card>
  )
}
