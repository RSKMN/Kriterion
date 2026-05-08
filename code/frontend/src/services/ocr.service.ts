import { createWorker } from 'tesseract.js'

import { parseReceiptText, ExtractedData } from '@/utils/ocr-parser'

export interface OCRProgress {
  status: string
  progress: number
}

export const ocrService = {
  async processReceipt(
    image: string | File,
    onProgress?: (progress: OCRProgress) => void
  ): Promise<ExtractedData> {
    const worker = await createWorker('eng', 1, {
      logger: (m) => {
        if (m.status === 'recognizing text') {
          onProgress?.({ status: 'Processing...', progress: Math.round(m.progress * 100) })
        } else {
          onProgress?.({ status: m.status, progress: 0 })
        }
      },
    })

    try {
      const { data: { text } } = await worker.recognize(image)
      await worker.terminate()
      
      return parseReceiptText(text)
    } catch (error) {
      await worker.terminate()
      throw error
    }
  },
}
