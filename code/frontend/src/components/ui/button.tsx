import type { ButtonHTMLAttributes, DetailedHTMLProps } from 'react'

import { cn } from '@/utils/cn'

type ButtonProps = DetailedHTMLProps<ButtonHTMLAttributes<HTMLButtonElement>, HTMLButtonElement> & {
  variant?: 'default' | 'ghost'
}

export function Button({ className, variant = 'default', ...props }: ButtonProps) {
  return (
    <button
      className={cn(
        'inline-flex items-center justify-center rounded-md px-4 py-2 text-sm font-medium transition-colors',
        variant === 'default' && 'bg-primary text-primary-foreground',
        variant === 'ghost' && 'bg-transparent hover:bg-secondary',
        className,
      )}
      {...props}
    />
  )
}
