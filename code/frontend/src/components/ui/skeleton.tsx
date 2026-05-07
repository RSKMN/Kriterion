import type { HTMLAttributes } from 'react'

import { cn } from '@/utils/cn'

export function Skeleton({ className, ...props }: HTMLAttributes<HTMLDivElement>) {
  return <div {...props} className={cn('animate-pulse rounded-md bg-muted', className)} />
}