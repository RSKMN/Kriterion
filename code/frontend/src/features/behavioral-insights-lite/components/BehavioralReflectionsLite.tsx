import React from 'react'

import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import { LiteReflection } from '@/types/behavior-lite'

interface BehavioralReflectionsLiteProps {
  insights: LiteReflection[]
  fallbackMessage?: string
}

export function BehavioralReflectionsLite({ insights, fallbackMessage }: BehavioralReflectionsLiteProps) {
  const reflections = insights.length > 0 ? insights : []

  return (
    <div className="space-y-4">
      {reflections.length > 0 ? (
        reflections.map((reflection) => (
          <Card key={`${reflection.title}-${reflection.emphasis}`} className="border-none bg-white/70 shadow-sm backdrop-blur-sm dark:bg-zinc-900/60">
            <CardHeader className="pb-2">
              <div className="flex items-start justify-between gap-3">
                <CardTitle className="text-base font-semibold text-zinc-900 dark:text-zinc-50">{reflection.title}</CardTitle>
                <Badge variant="outline" className="capitalize">{reflection.emphasis}</Badge>
              </div>
            </CardHeader>
            <CardContent>
              <p className="text-sm leading-relaxed text-zinc-500">{reflection.description}</p>
            </CardContent>
          </Card>
        ))
      ) : (
        <Card className="border-dashed border-zinc-200 bg-white/70 shadow-sm backdrop-blur-sm dark:border-zinc-800 dark:bg-zinc-900/60">
          <CardContent className="p-5 text-sm text-zinc-500">
            {fallbackMessage || 'Behavioral insights improve as more transaction activity is analyzed.'}
          </CardContent>
        </Card>
      )}
    </div>
  )
}
