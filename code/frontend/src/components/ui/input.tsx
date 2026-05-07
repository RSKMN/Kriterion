import type { InputHTMLAttributes } from 'react'

import { cn } from '@/utils/cn'

type InputProps = InputHTMLAttributes<HTMLInputElement>

export function Input({ className, ...props }: InputProps) {
  return <input className={cn('w-full rounded-md border bg-background px-3 py-2 text-sm', className)} {...props} />
}
