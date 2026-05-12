export interface BehavioralMetrics {
    transactionFrequency: number;
    decisionDensity: number;
    spendingVolatility: number;
    categoryInstability: number;
    discretionarySpendingRatio: number;
    recurringObligationPressure: number;
    lateNightSpendingRatio: number;
    spendingBurstFrequency: number;
    averageTransactionFragmentation: number;
    transactionRhythmConsistency: number;
    lastComputedAt: string;
    hourlyPatterns: Record<number, number>;
    dailyPatterns: Record<string, number>;
    weeklyPatterns: Record<number, number>;
    monthlyPatterns: Record<number, number>;
    postSalarySpendingShift: number;
}

export interface BehavioralPattern {
    id: number;
    patternName: string;
    confidenceScore: number;
    detectedAt: string;
    lastObservedAt: string;
    evidenceMetadata: string;
    isActive: boolean;
}

export interface LoadFactor {
    name: string;
    value: number;
    impact: 'HIGH' | 'MEDIUM' | 'LOW';
}

export interface CognitiveLoadResponse {
    cognitiveLoadScore: number;
    volatilityScore: number;
    rhythmStabilityScore: number;
    spendingStructureScore: number;
    obligationPressureScore: number;
    factors: LoadFactor[];
    interpretations: string[];
    status?: string;
}

export interface FinancialReflection {
    title: string;
    description: string;
    confidenceScore: number;
    supportingSignals: string[];
    detectedPatterns: string[];
    timeRange: string;
    priority: number;
}

export interface NarrativeResponse {
    narrative: string;
    summary: string;
    confidence: string;
    isFallback: boolean;
}

export interface PredictionResponse {
    overspendingRisk: string;
    trendDirection: string;
    confidenceScore: number;
    confidenceState: string;
    forecastNarrative: string;
    supportingSignals: string[];
    status: string;
    isFallback: boolean;
}
