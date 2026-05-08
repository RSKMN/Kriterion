import { Upload, X, FileImage, Image as ImageIcon } from 'lucide-react'
import React, { useCallback, useState } from 'react'

import { Button } from '@/components/ui/button'
import { Card } from '@/components/ui/card'
import { cn } from '@/lib/utils'

interface ReceiptUploaderProps {
  onFileSelect: (file: File) => void
  onReset: () => void
  selectedFile: File | null
  previewUrl: string | null
  disabled?: boolean
}

export function ReceiptUploader({
  onFileSelect,
  onReset,
  selectedFile,
  previewUrl,
  disabled
}: ReceiptUploaderProps) {
  const [isDragging, setIsDragging] = useState(false)

  const handleDragOver = useCallback((e: React.DragEvent) => {
    e.preventDefault()
    if (disabled) return
    setIsDragging(true)
  }, [disabled])

  const handleDragLeave = useCallback((e: React.DragEvent) => {
    e.preventDefault()
    setIsDragging(false)
  }, [])

  const handleDrop = useCallback((e: React.DragEvent) => {
    e.preventDefault()
    setIsDragging(false)
    if (disabled) return

    const file = e.dataTransfer.files?.[0]
    if (file && file.type.startsWith('image/')) {
      onFileSelect(file)
    }
  }, [onFileSelect, disabled])

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0]
    if (file) {
      onFileSelect(file)
    }
  }

  return (
    <div className="w-full max-w-2xl mx-auto">
      {!selectedFile ? (
        <div
          onDragOver={handleDragOver}
          onDragLeave={handleDragLeave}
          onDrop={handleDrop}
          role="button"
          tabIndex={0}
          className={cn(
            "relative border-2 border-dashed rounded-xl p-12 transition-all duration-200 flex flex-col items-center justify-center gap-4 text-center cursor-pointer",
            isDragging ? "border-primary bg-primary/5 scale-[1.01]" : "border-muted-foreground/20 hover:border-primary/50 hover:bg-muted/50",
            disabled && "opacity-50 cursor-not-allowed pointer-events-none"
          )}
          onClick={() => document.getElementById('receipt-upload')?.click()}
          onKeyDown={(e) => {
            if (e.key === 'Enter' || e.key === ' ') {
              e.preventDefault()
              document.getElementById('receipt-upload')?.click()
            }
          }}
        >
          <input
            id="receipt-upload"
            type="file"
            className="hidden"
            accept="image/*"
            onChange={handleFileChange}
            disabled={disabled}
          />
          <div className="p-4 rounded-full bg-primary/10 text-primary">
            <Upload className="w-8 h-8" />
          </div>
          <div className="space-y-1">
            <p className="text-lg font-medium">Click to upload or drag and drop</p>
            <p className="text-sm text-muted-foreground">PNG, JPG or WEBP (max. 10MB)</p>
          </div>
        </div>
      ) : (
        <Card className="relative overflow-hidden border-2 border-primary/20 bg-muted/30">
          <div className="absolute top-2 right-2 z-10">
            <Button
              variant="destructive"
              size="icon"
              className="h-8 w-8 rounded-full shadow-lg"
              onClick={(e) => {
                e.stopPropagation()
                onReset()
              }}
              disabled={disabled}
            >
              <X className="w-4 h-4" />
            </Button>
          </div>
          
          <div className="p-4 flex flex-col md:flex-row gap-6 items-center">
            {previewUrl ? (
              <div className="w-full md:w-48 aspect-[3/4] rounded-lg overflow-hidden border bg-background flex items-center justify-center">
                <img
                  src={previewUrl}
                  alt="Receipt preview"
                  className="w-full h-full object-contain"
                />
              </div>
            ) : (
              <div className="w-full md:w-48 aspect-[3/4] rounded-lg bg-muted flex items-center justify-center">
                <ImageIcon className="w-12 h-12 text-muted-foreground/30" />
              </div>
            )}
            
            <div className="flex-1 space-y-3 text-center md:text-left">
              <div className="space-y-1">
                <h3 className="font-semibold text-lg flex items-center justify-center md:justify-start gap-2">
                  <FileImage className="w-5 h-5 text-primary" />
                  {selectedFile.name}
                </h3>
                <p className="text-sm text-muted-foreground">
                  {(selectedFile.size / 1024 / 1024).toFixed(2)} MB • {selectedFile.type.split('/')[1].toUpperCase()}
                </p>
              </div>
              {!disabled && (
                <Button 
                  variant="outline" 
                  size="sm" 
                  onClick={() => document.getElementById('receipt-upload')?.click()}
                >
                  Change File
                </Button>
              )}
            </div>
          </div>
        </Card>
      )}
    </div>
  )
}
