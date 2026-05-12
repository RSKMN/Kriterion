export interface PredictiveSeriesPoint {
  label: string;
  value: number;
}

export interface PredictiveInsight {
  title: string;
  description: string;
  category: 'overspending' | 'subscription' | 'volatility' | 'savings' | 'end_of_month' | 'insufficient_data' | 'stable';
  weight: number;
}

export interface PredictiveForecast {
  overspendingRisk: 'low' | 'moderate' | 'high';
  forecastDaysRemaining: number | null;
  volatilityTrend: 'stable' | 'increasing' | 'decreasing';
  subscriptionPressureTrend: 'stable' | 'increasing' | 'decreasing';
  savingsTrend: 'improving' | 'stable' | 'declining';
  confidence: number;
  insights: PredictiveInsight[];
  dataStatus: 'insufficient_data' | 'partial' | 'ready';
  generatedAt: string;
  fallback: boolean;
}
