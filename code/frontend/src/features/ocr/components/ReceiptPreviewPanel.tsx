import { Maximize2, Minimize2, ZoomIn, ZoomOut, RotateCw } from 'lucide-react'
import { useState } from 'react'

import { Button } from '@/components/ui/button'
import { Card } from '@/components/ui/card'
import { cn } from '@/lib/utils'

interface ReceiptPreviewPanelProps {
  url: string
  className?: string
}

export function ReceiptPreviewPanel({ url, className }: ReceiptPreviewPanelProps) {
  const [scale, setScale] = useState(1)
  const [rotation, setRotation] = useState(0)
  const [isFullscreen, setIsFullscreen] = useState(false)

  const handleZoomIn = () => setScale(prev => Math.min(prev + 0.25, 3))
  const handleZoomOut = () => setScale(prev => Math.max(prev - 0.25, 0.5))
  const handleRotate = () => setRotation(prev => (prev + 90) % 360)
  const toggleFullscreen = () => setIsFullscreen(!isFullscreen)

  return (
    <Card className={cn(
      "relative overflow-hidden bg-slate-900 border-slate-800 flex flex-col",
      isFullscreen ? "fixed inset-0 z-50 rounded-none" : "h-[calc(100vh-200px)] sticky top-24",
      className
    )}>
      {/* Controls */}
      <div className="absolute top-4 right-4 z-20 flex gap-2">
        <div className="flex bg-black/50 backdrop-blur-md rounded-full p-1 border border-white/10">
          <Button variant="ghost" size="icon" className="h-8 w-8 text-white hover:bg-white/20 rounded-full" onClick={handleZoomOut}>
            <ZoomOut className="h-4 w-4" />
          </Button>
          <Button variant="ghost" size="icon" className="h-8 w-8 text-white hover:bg-white/20 rounded-full" onClick={handleZoomIn}>
            <ZoomIn className="h-4 w-4" />
          </Button>
          <Button variant="ghost" size="icon" className="h-8 w-8 text-white hover:bg-white/20 rounded-full" onClick={handleRotate}>
            <RotateCw className="h-4 w-4" />
          </Button>
          <Button variant="ghost" size="icon" className="h-8 w-8 text-white hover:bg-white/20 rounded-full" onClick={toggleFullscreen}>
            {isFullscreen ? <Minimize2 className="h-4 w-4" /> : <Maximize2 className="h-4 w-4" />}
          </Button>
        </div>
      </div>

      {/* Image Area */}
      <div className="flex-1 overflow-auto p-8 flex items-center justify-center cursor-grab active:cursor-grabbing">
        <div 
          className="transition-transform duration-200 ease-out shadow-2xl"
          style={{ 
            transform: `scale(${scale}) rotate(${rotation}deg)`,
          }}
        >
          <img
            src={url}
            alt="Receipt Preview"
            className="max-w-full max-h-full object-contain rounded-sm"
            onDragStart={(e) => e.preventDefault()}
          />
        </div>
      </div>

      {/* Footer Info */}
      <div className="px-4 py-2 bg-black/40 border-t border-white/5 backdrop-blur-sm text-[10px] text-slate-400 flex justify-between">
        <span>PREVIEW MODE</span>
        <span>SCALE: {Math.round(scale * 100)}%</span>
      </div>
    </Card>
  )
}
