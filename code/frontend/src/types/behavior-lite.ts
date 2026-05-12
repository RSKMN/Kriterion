export interface LiteSeriesPoint {
  label: string
  value: number
}

export interface LiteBurstWindow {
  label: string
  startAt: string
  endAt: string
  transactionCount: number
  totalAmount: number
}

export interface LiteReflection {
  title: string
  description: string
  emphasis: string
}

export interface BehaviorLiteSummary {
  volatility: number
  lateNightRatio: number
  subscriptionPressure: number
  savingsTrend: number
  savingsTrendLabel: string
  hourlyDistribution: Record<number, number>
  weeklySpending: LiteSeriesPoint[]
  savingsSeries: LiteSeriesPoint[]
  bursts: LiteBurstWindow[]
  insights: LiteReflection[]
  transactionCount: number
  incomeCount: number
  expenseCount: number
  dataStatus: string
  generatedAt: string
  fallback: boolean
}

export interface PredictLiteForecast {
  riskLevel: string
  trendDirection: string
  confidence: number
  narrative: string
  signals: string[]
  dataStatus: string
  fallback: boolean
}
