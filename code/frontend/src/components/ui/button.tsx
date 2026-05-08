import type { ButtonHTMLAttributes, DetailedHTMLProps } from 'react'

import { cn } from '@/utils/cn'

type ButtonProps = DetailedHTMLProps<ButtonHTMLAttributes<HTMLButtonElement>, HTMLButtonElement> & {
  variant?: 'default' | 'ghost' | 'outline' | 'secondary' | 'destructive'
}

export function Button({ className, variant = 'default', ...props }: ButtonProps) {
  return (
    <button
      className={cn(
        'inline-flex items-center justify-center rounded-md px-4 py-2 text-sm font-medium transition-colors disabled:opacity-50 disabled:pointer-events-none',
        variant === 'default' && 'bg-primary text-primary-foreground hover:bg-primary/90',
        variant === 'ghost' && 'bg-transparent hover:bg-secondary',
        variant === 'outline' && 'border border-input bg-background hover:bg-accent hover:text-accent-foreground',
        variant === 'secondary' && 'bg-secondary text-secondary-foreground hover:bg-secondary/80',
        variant === 'destructive' && 'bg-destructive text-destructive-foreground hover:bg-destructive/90',
        className,
      )}
      {...props}
    />
  )
}
