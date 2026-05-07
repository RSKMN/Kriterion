import type { LucideIcon } from 'lucide-react'
import React from 'react'

import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { cn } from '@/utils/cn'

interface SummaryCardProps {
  label: string
  value: string
  description?: string
  icon?: LucideIcon
  iconClassName?: string
  valueClassName?: string
}

export function SummaryCard({ label, value, description, icon: Icon, iconClassName = '', valueClassName = '' }: SummaryCardProps) {
  return (
    <Card className="overflow-hidden border-border/60 bg-gradient-to-br from-background to-muted/20">
      <CardHeader className="space-y-4 pb-4">
        <div className="flex items-center justify-between gap-4">
          <CardDescription className="text-sm font-medium uppercase tracking-[0.18em]">{label}</CardDescription>
          {Icon ? (
            <div className={cn('flex h-11 w-11 items-center justify-center rounded-full', iconClassName)}>
              <Icon className="h-5 w-5" />
            </div>
          ) : null}
        </div>
        <CardTitle className={cn('text-3xl tabular-nums tracking-tight sm:text-4xl', valueClassName)}>{value}</CardTitle>
      </CardHeader>
      <CardContent>
        {description ? <CardDescription className="max-w-xs text-sm leading-6">{description}</CardDescription> : null}
      </CardContent>
    </Card>
  )
}
