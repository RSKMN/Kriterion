import { getSimilarity } from './fuzzy-match'

export interface ConfidenceScore {
  merchant: number
  amount: number
  date: number
  category: number
}

export interface CategorySuggestion {
  categoryId?: number
  categoryName: string
  confidence: number
  isAiEnhanced?: boolean
}

// Expanded merchant keywords to category mappings with priority
const CATEGORY_MAPPINGS: Record<string, { keywords: string[], categoryName: string }> = {
  'Food & Dining': {
    keywords: ['RESTAURANT', 'CAFE', 'COFFEE', 'STARBUCKS', 'MCDONALD', 'SUBWAY', 'BURGER', 'PIZZA', 'DINER', 'KITCHEN', 'TACO', 'BAKERY', 'SWEETS', 'DONUT', 'GRILL', 'BAR', 'PUB'],
    categoryName: 'Food & Dining'
  },
  'Groceries': {
    keywords: ['WALMART', 'TARGET', 'KROGER', 'SAFEWAY', 'WHOLE FOODS', 'MARKET', 'GROCERY', 'SUPERMARKET', 'COSTCO', 'ALDI', 'LIDL', 'TRADER JOE', 'PUBLIX', '7-ELEVEN', 'WHEAT', 'FRESH'],
    categoryName: 'Groceries'
  },
  'Transportation': {
    keywords: ['UBER', 'LYFT', 'TAXI', 'SHELL', 'EXXON', 'CHEVRON', 'GAS', 'FUEL', 'SUBWAY', 'METRO', 'PARKING', 'TRAIN', 'BUS', 'TRANSIT', 'RAIL', 'AIRLINE', 'FLIGHT', 'DELTA', 'UNITED'],
    categoryName: 'Transportation'
  },
  'Shopping': {
    keywords: ['AMAZON', 'EBAY', 'NIKE', 'ADIDAS', 'ZARA', 'H&M', 'MACY', 'STORE', 'SHOP', 'RETAIL', 'MALL', 'BOUTIQUE', 'FASHION', 'CLOTHING', 'APPLE', 'BEST BUY', 'ELECTRONICS'],
    categoryName: 'Shopping'
  },
  'Entertainment': {
    keywords: ['NETFLIX', 'SPOTIFY', 'CINEMA', 'THEATER', 'MUSEUM', 'CONCERT', 'TICKET', 'GAME', 'XBOX', 'PLAYSTATION', 'STEAM', 'DISNEY', 'HBO', 'HULU', 'EVENT'],
    categoryName: 'Entertainment'
  },
  'Utilities': {
    keywords: ['ELECTRIC', 'WATER', 'GAS', 'INTERNET', 'PHONE', 'MOBILE', 'VERIZON', 'AT&T', 'COMCAST', 'UTILITY', 'BILL', 'POWER', 'ENERGY', 'WASTE'],
    categoryName: 'Utilities'
  },
  'Health': {
    keywords: ['PHARMACY', 'DRUGSTORE', 'CVS', 'WALGREENS', 'HOSPITAL', 'CLINIC', 'DOCTOR', 'DENTIST', 'MEDICAL', 'HEALTH', 'GYM', 'FITNESS', 'THERAPY', 'DENTAL'],
    categoryName: 'Health'
  }
}

export function calculateConfidence(text: string, value: any, type: keyof ConfidenceScore): number {
  if (!value || value === 0 || value === '') return 0
  
  const upperText = text.toUpperCase()
  
  switch (type) {
    case 'merchant': {
      const topContent = upperText.substring(0, 150)
      return topContent.includes(value.toUpperCase()) ? 0.92 : 0.65
    }
    case 'amount': {
      // Improved amount confidence: check for total keywords in proximity
      const amountPos = upperText.indexOf(value.toString())
      const window = upperText.substring(Math.max(0, amountPos - 20), amountPos)
      const hasTotalKeyword = ['TOTAL', 'DUE', 'AMT', 'PAID'].some(k => window.includes(k))
      return hasTotalKeyword ? 0.98 : 0.75
    }
    case 'date': {
      return 0.88
    }
    case 'category': {
      return 0.82
    }
    default:
      return 0.5
  }
}

export function suggestCategory(merchantName: string): CategorySuggestion {
  const upperMerchant = merchantName.toUpperCase()
  let bestMatch = { categoryName: 'Miscellaneous', score: 0 }
  
  // Try exact keyword matching first
  for (const [name, mapping] of Object.entries(CATEGORY_MAPPINGS)) {
    if (mapping.keywords.some(keyword => upperMerchant.includes(keyword))) {
      return {
        categoryName: name,
        confidence: 0.9,
        isAiEnhanced: false
      }
    }
  }
  
  // Try fuzzy matching as "AI" backup
  for (const [name, mapping] of Object.entries(CATEGORY_MAPPINGS)) {
    for (const keyword of mapping.keywords) {
      const similarity = getSimilarity(upperMerchant, keyword)
      if (similarity > 0.7 && similarity > bestMatch.score) {
        bestMatch = { categoryName: name, score: similarity }
      }
    }
  }
  
  if (bestMatch.score > 0.7) {
    return {
      categoryName: bestMatch.categoryName,
      confidence: bestMatch.score,
      isAiEnhanced: true
    }
  }
  
  return {
    categoryName: 'Miscellaneous',
    confidence: 0.3,
    isAiEnhanced: false
  }
}

export function normalizeAmount(amountStr: string): number {
  let cleaned = amountStr.replace(/[^0-9.,]/g, '').trim()
  if (cleaned.includes(',') && cleaned.includes('.')) {
    cleaned = cleaned.replace(',', '')
  } else if (cleaned.includes(',') && cleaned.split(',').pop()?.length === 2) {
    cleaned = cleaned.replace(',', '.')
  }
  return parseFloat(cleaned) || 0
}

/**
 * AI-Assisted amount detector: attempts to differentiate between SUBTOTAL, TAX, and TOTAL.
 */
export function aiDetectTotal(text: string, detectedAmounts: number[]): number {
  if (detectedAmounts.length === 0) return 0
  if (detectedAmounts.length === 1) return detectedAmounts[0]
  
  const lines = text.split('\n').map(l => l.toUpperCase())
  
  // Heuristic: Look for the line containing "TOTAL" and find the amount on that line or next line
  for (let i = 0; i < lines.length; i++) {
    if (lines[i].includes('TOTAL') && !lines[i].includes('SUBTOTAL')) {
      // Find the number in this line
      const match = lines[i].match(/(\d+[.,]\d{2})/)
      if (match) return normalizeAmount(match[1])
      
      // Check next line
      if (i + 1 < lines.length) {
        const nextMatch = lines[i+1].match(/(\d+[.,]\d{2})/)
        if (nextMatch) return normalizeAmount(nextMatch[1])
      }
    }
  }
  
  // Fallback to max amount (classic deterministic)
  return Math.max(...detectedAmounts)
}
