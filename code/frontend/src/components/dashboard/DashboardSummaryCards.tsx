import { ArrowDownRight, ArrowUpRight, Wallet, PiggyBank } from 'lucide-react'

import { Skeleton } from '@/components/ui/skeleton'
import type { DashboardSummary } from '@/types'
import { cn } from '@/utils/cn'

import { SummaryCard } from './SummaryCard'

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
    <div className="grid gap-4 md:grid-cols-4">
      {Array.from({ length: 4 }).map((_, index) => (
        <div key={index} className="overflow-hidden">
          <div className="p-4">
            <Skeleton className="h-4 w-24" />
            <Skeleton className="mt-4 h-8 w-36" />
            <Skeleton className="mt-6 h-4 w-32" />
          </div>
        </div>
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
      label: 'Total balance',
      value: formatCurrency(summary.totalBalance ?? summary.remainingBalance),
      description: 'Current account balance for the selected period.',
      icon: Wallet,
      iconClassName: cn('bg-sky-500/10', balanceTone),
      valueClassName: balanceTone,
    },
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
      label: 'Savings',
      value: formatCurrency(summary.savings ?? summary.totalIncome - summary.totalExpense),
      description: 'Net saved amount (income minus expenses).',
      icon: PiggyBank,
      iconClassName: 'bg-amber-500/10 text-amber-600',
    },
  ]

  return (
    <div className="grid gap-4 md:grid-cols-4">
      {cards.map((card) => {
        const Icon = card.icon

        return (
          <SummaryCard
            key={card.label}
            label={card.label}
            value={card.value}
            description={card.description}
            icon={Icon}
            iconClassName={card.iconClassName}
            valueClassName={card.valueClassName}
          />
        )
      })}
    </div>
  )
}