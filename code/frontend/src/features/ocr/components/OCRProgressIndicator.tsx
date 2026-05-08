import { Loader2 } from 'lucide-react'

import { Card, CardContent } from '@/components/ui/card'
import { Progress } from '@/components/ui/progress'

interface OCRProgressIndicatorProps {
  status: string
  progress: number
}

export function OCRProgressIndicator({ status, progress }: OCRProgressIndicatorProps) {
  return (
    <Card className="w-full max-w-md mx-auto border-none shadow-none bg-transparent">
      <CardContent className="pt-6 space-y-4">
        <div className="flex items-center justify-between text-sm font-medium">
          <div className="flex items-center gap-2">
            <Loader2 className="w-4 h-4 animate-spin text-primary" />
            <span>{status}</span>
          </div>
          <span className="tabular-nums">{progress}%</span>
        </div>
        <Progress value={progress} className="h-2" />
        <p className="text-xs text-center text-muted-foreground animate-pulse">
          Analyzing receipt using on-device OCR...
        </p>
      </CardContent>
    </Card>
  )
}
