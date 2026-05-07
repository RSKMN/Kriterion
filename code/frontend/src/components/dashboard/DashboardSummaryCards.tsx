import { ArrowDownRight, ArrowUpRight, Wallet } from 'lucide-react'

import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Skeleton } from '@/components/ui/skeleton'
import type { DashboardSummary } from '@/types'
import { cn } from '@/utils/cn'

interface DashboardSummaryCardsProps {
  summary: DashboardSummary | null
  loading?: boolean
}

const currencyFormatter = new Intl.NumberFormat('en-US', {
  style: 'currency',
  currency: 'USD',
  maximumFractionDigits: 2,
})

function formatCurrency(value: number) {
  return currencyFormatter.format(value)
}

function SummarySkeleton() {
  return (
    <div className="grid gap-4 md:grid-cols-3">
      {Array.from({ length: 3 }).map((_, index) => (
        <Card key={index} className="overflow-hidden">
          <CardHeader>
            <Skeleton className="h-4 w-24" />
            <Skeleton className="h-8 w-36" />
          </CardHeader>
          <CardContent>
            <Skeleton className="h-4 w-32" />
          </CardContent>
        </Card>
      ))}
    </div>
  )
}

export function DashboardSummaryCards({ summary, loading = false }: DashboardSummaryCardsProps) {
  if (loading || !summary) {
    return <SummarySkeleton />
  }

  const balanceTone = summary.remainingBalance >= 0 ? 'text-emerald-600' : 'text-destructive'

  const cards = [
    {
      label: 'Total income',
      value: formatCurrency(summary.totalIncome),
      description: 'All credited transactions across your account.',
      icon: ArrowUpRight,
      iconClassName: 'bg-emerald-500/10 text-emerald-600',
    },
    {
      label: 'Total expense',
      value: formatCurrency(summary.totalExpense),
      description: 'All debits and outgoing payments.',
      icon: ArrowDownRight,
      iconClassName: 'bg-rose-500/10 text-rose-600',
    },
    {
      label: 'Remaining balance',
      value: formatCurrency(summary.remainingBalance),
      description: 'Income minus expense for the selected period.',
      icon: Wallet,
      iconClassName: cn('bg-sky-500/10', balanceTone),
      valueClassName: balanceTone,
    },
  ]

  return (
    <div className="grid gap-4 md:grid-cols-3">
      {cards.map((card) => {
        const Icon = card.icon

        return (
          <Card key={card.label} className="overflow-hidden border-border/60 bg-gradient-to-br from-background to-muted/20">
            <CardHeader className="space-y-4 pb-4">
              <div className="flex items-center justify-between gap-4">
                <CardDescription className="text-sm font-medium uppercase tracking-[0.18em]">
                  {card.label}
                </CardDescription>
                <div className={cn('flex h-11 w-11 items-center justify-center rounded-full', card.iconClassName)}>
                  <Icon className="h-5 w-5" />
                </div>
              </div>
              <CardTitle className={cn('text-3xl tabular-nums tracking-tight sm:text-4xl', card.valueClassName)}>
                {card.value}
              </CardTitle>
            </CardHeader>
            <CardContent>
              <CardDescription className="max-w-xs text-sm leading-6">{card.description}</CardDescription>
            </CardContent>
          </Card>
        )
      })}
    </div>
  )
}