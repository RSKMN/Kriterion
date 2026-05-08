import { Receipt, ArrowLeft, History, Search, FileText } from 'lucide-react'
import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { toast } from 'sonner'

import { Button } from '@/components/ui/button'
import { ExtractedDataForm } from '@/features/ocr/components/ExtractedDataForm'
import { OCRProgressIndicator } from '@/features/ocr/components/OCRProgressIndicator'
import { ReceiptPreviewPanel } from '@/features/ocr/components/ReceiptPreviewPanel'
import { ReceiptUploader } from '@/features/ocr/components/ReceiptUploader'
import { ocrService, OCRProgress } from '@/services/ocr.service'
import { useOCRStore } from '@/store/ocr.store'
import { useTransactionStore } from '@/store/transaction.store'
import { ExtractedData } from '@/utils/ocr-parser'

export default function ReceiptScannerPage() {
  const navigate = useNavigate()
  const { createTransaction } = useTransactionStore()
  const { addScan } = useOCRStore()
  
  const [selectedFile, setSelectedFile] = useState<File | null>(null)
  const [previewUrl, setPreviewUrl] = useState<string | null>(null)
  const [ocrStatus, setOcrStatus] = useState<OCRProgress | null>(null)
  const [extractedData, setExtractedData] = useState<ExtractedData | null>(null)
  const [isProcessing, setIsProcessing] = useState(false)
  const [isSaving, setIsSaving] = useState(false)

  useEffect(() => {
    if (!selectedFile) {
      setPreviewUrl(null)
      return
    }

    const url = URL.createObjectURL(selectedFile)
    setPreviewUrl(url)

    return () => URL.revokeObjectURL(url)
  }, [selectedFile])

  const handleFileSelect = async (file: File) => {
    setSelectedFile(file)
    setExtractedData(null)
    setIsProcessing(true)

    try {
      const result = await ocrService.processReceipt(file, (progress) => {
        setOcrStatus(progress)
      })
      setExtractedData(result)
      toast.success('Extraction complete! Please verify the details.')
    } catch (error) {
      console.error('OCR Error:', error)
      toast.error('Failed to process receipt. Please try again.')
    } finally {
      setIsProcessing(false)
      setOcrStatus(null)
    }
  }

  const handleReset = () => {
    setSelectedFile(null)
    setExtractedData(null)
    setOcrStatus(null)
    setIsProcessing(false)
  }

  const handleSaveTransaction = async (data: any) => {
    setIsSaving(true)
    try {
      await createTransaction(data)
      addScan({
        merchantName: data.merchantName || data.title,
        amount: data.amount,
        date: data.transactionDate,
        status: 'completed'
      })
      toast.success('Transaction confirmed and saved!')
      navigate('/transactions')
    } catch (error: any) {
      toast.error(error.message || 'Failed to save transaction')
    } finally {
      setIsSaving(false)
    }
  }

  return (
    <div className="max-w-[1600px] mx-auto space-y-6 pb-12">
      {/* Dynamic Header */}
      <div className="flex flex-col gap-4 md:flex-row md:items-center md:justify-between px-2">
        <div className="space-y-1">
          <div className="flex items-center gap-2 text-indigo-600 mb-1">
            <div className="p-1.5 bg-indigo-50 rounded-lg">
              <Receipt className="w-4 h-4" />
            </div>
            <span className="text-[10px] font-bold uppercase tracking-[0.2em]">Verification Workflow</span>
          </div>
          <h1 className="text-3xl font-bold tracking-tight">
            {extractedData ? 'Verify Extraction' : 'Scan Receipt'}
          </h1>
          <p className="text-sm text-muted-foreground">
            {extractedData 
              ? 'Review the side-by-side preview to ensure accuracy.' 
              : 'Upload or drop a receipt image to begin automated extraction.'}
          </p>
        </div>
        
        <div className="flex items-center gap-3">
          {!extractedData && (
            <Button variant="outline" size="sm" onClick={() => navigate('/transactions')}>
              <History className="w-4 h-4 mr-2" />
              History
            </Button>
          )}
          <Button variant="ghost" size="sm" onClick={() => navigate(-1)}>
            <ArrowLeft className="w-4 h-4 mr-2" />
            Back
          </Button>
        </div>
      </div>

      {!extractedData ? (
        /* Initial State: Upload View */
        <div className="max-w-3xl mx-auto w-full pt-8 space-y-8">
          <div className="text-center space-y-4 mb-12">
            <div className="mx-auto w-16 h-16 bg-primary/10 rounded-full flex items-center justify-center text-primary">
              <Search className="w-8 h-8" />
            </div>
            <h2 className="text-xl font-semibold">Ready to process?</h2>
            <p className="text-muted-foreground max-w-sm mx-auto">
              Our intelligent OCR will extract the merchant, amount, and date automatically.
            </p>
          </div>
          
          <ReceiptUploader
            selectedFile={selectedFile}
            previewUrl={previewUrl}
            onFileSelect={handleFileSelect}
            onReset={handleReset}
            disabled={isProcessing}
          />

          {isProcessing && ocrStatus && (
            <div className="max-w-md mx-auto pt-4">
              <OCRProgressIndicator 
                status={ocrStatus.status} 
                progress={ocrStatus.progress} 
              />
            </div>
          )}
        </div>
      ) : (
        /* Extraction State: Side-by-Side Review */
        <div className="grid grid-cols-1 lg:grid-cols-12 gap-6 items-start animate-in fade-in zoom-in-95 duration-500">
          {/* Left: Image Preview */}
          <div className="lg:col-span-5 xl:col-span-4">
            <div className="space-y-4">
              <div className="flex items-center gap-2 text-xs font-semibold text-muted-foreground uppercase tracking-widest px-1">
                <FileText className="w-3 h-3" />
                Original Document
              </div>
              <ReceiptPreviewPanel url={previewUrl!} />
            </div>
          </div>

          {/* Right: Review Form */}
          <div className="lg:col-span-7 xl:col-span-8">
            <div className="space-y-4">
              <div className="flex items-center gap-2 text-xs font-semibold text-muted-foreground uppercase tracking-widest px-1">
                <Search className="w-3 h-3" />
                Extracted Data Verification
              </div>
              <ExtractedDataForm
                data={extractedData}
                onSubmit={handleSaveTransaction}
                onCancel={handleReset}
                loading={isSaving}
              />
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
