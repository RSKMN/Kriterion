interface ErrorStateProps {
  title?: string
  message?: string
}

export function ErrorState({ title = 'Something went wrong', message = 'Please try again later.' }: ErrorStateProps) {
  return (
    <div className="rounded-lg border border-destructive/30 bg-destructive/5 p-4 text-sm">
      <div className="font-medium text-destructive">{title}</div>
      <div className="mt-1 text-muted-foreground">{message}</div>
    </div>
  )
}
