import { format, parse } from 'date-fns'

import { 
  ConfidenceScore, 
  calculateConfidence, 
  suggestCategory, 
  CategorySuggestion,
  normalizeAmount,
  aiDetectTotal
} from './ocr-heuristics'

export interface ExtractedData {
  merchantName: string
  totalAmount: number
  transactionDate: string
  confidence: ConfidenceScore
  suggestion: CategorySuggestion
  rawText: string
  isAiOptimized: boolean
}

export function parseReceiptText(text: string): ExtractedData {
  const lines = text.split('\n').map((line) => line.trim()).filter((line) => line.length > 0)
  
  // 1. Merchant Name Extraction (AI Optimized)
  let merchantName = ''
  if (lines.length > 0) {
    // Check first 3 lines for anything that looks like a business name
    for (let i = 0; i < Math.min(3, lines.length); i++) {
      const line = lines[i]
      if (line.length > 2 && !line.match(/\d{2,}/) && !['RECEIPT', 'INVOICE', 'DATE'].some(k => line.toUpperCase().includes(k))) {
        merchantName = line
        break
      }
    }
  }

  // 2. Amount Extraction (AI Assisted)
  const amountRegex = /(\d+[.,]\d{2})/g
  const allMatches = text.match(amountRegex) || []
  const detectedAmounts = allMatches.map(amt => normalizeAmount(amt))
  
  const totalAmount = aiDetectTotal(text, detectedAmounts)

  // 3. Date Extraction
  let transactionDate = format(new Date(), 'yyyy-MM-dd')
  const datePatterns = [
    /(\d{2}[/-]\d{2}[/-]\d{4})/,
    /(\d{4}[/-]\d{2}[/-]\d{2})/,
    /([A-Z][a-z]{2,8}\s+\d{1,2},?\s+\d{4})/,
    /(\d{1,2}\s+[A-Z][a-z]{2,8}\s+\d{4})/
  ]

  for (const pattern of datePatterns) {
    const match = text.match(pattern)
    if (match) {
      try {
        const dateStr = match[1]
        let parsedDate: Date | null = null
        if (dateStr.includes('/')) {
          parsedDate = parse(dateStr, 'dd/MM/yyyy', new Date())
          if (isNaN(parsedDate.getTime())) parsedDate = parse(dateStr, 'MM/dd/yyyy', new Date())
        } else if (dateStr.includes('-')) {
          parsedDate = parse(dateStr, 'yyyy-MM-dd', new Date())
          if (isNaN(parsedDate.getTime())) parsedDate = parse(dateStr, 'dd-MM-yyyy', new Date())
        } else {
          parsedDate = new Date(dateStr)
        }
        if (parsedDate && !isNaN(parsedDate.getTime())) {
          transactionDate = format(parsedDate, 'yyyy-MM-dd')
          break
        }
      } catch {
        // Ignore parsing errors for this pattern
      }
    }
  }

  // Finalize data with AI heuristics
  const resultMerchant = merchantName || 'Unknown Merchant'
  const suggestion = suggestCategory(resultMerchant)

  return {
    merchantName: resultMerchant,
    totalAmount,
    transactionDate,
    confidence: {
      merchant: calculateConfidence(text, resultMerchant, 'merchant'),
      amount: calculateConfidence(text, totalAmount, 'amount'),
      date: calculateConfidence(text, transactionDate, 'date'),
      category: suggestion.confidence
    },
    suggestion,
    rawText: text,
    isAiOptimized: true
  }
}
