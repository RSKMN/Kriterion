import { apiClient } from './api';

export interface SpendingRhythmAnalysis {
  rhythmPattern: 'structured' | 'stable' | 'variable' | 'bursty' | 'irregular' | 'unknown';
  timeOfDayPreference: 'early' | 'mid' | 'late' | 'night' | 'unknown';
  irregularityScore: number;
  lateNightTransactionRate: number;
  hasNightSpendingPattern: boolean;
  temporalObservation: string;
  confidence: number;
}

export interface TransactionDensityAnalysis {
  averageTransactionsPerDay: number;
  averageTransactionsPerWeek: number;
  densityLevel: 'low' | 'moderate' | 'high' | 'very_high' | 'unknown';
  hasBurstPatterns: boolean;
  burstFrequency: number;
  densityObservation: string;
  confidence: number;
}

export interface SubscriptionPressureAnalysis {
  pressureScore: number;
  pressureLevel: 'low' | 'moderate' | 'high' | 'unknown';
  activeSubscriptionCount: number;
  pressureObservation: string;
}

export interface FinancialStructureAnalysis {
  discretionaryRatio: number;
  recurringRatio: number;
  structureStability: 'stable' | 'shifting' | 'volatile' | 'unknown';
  hasConsistentStructure: boolean;
  structuralObservation: string;
  subscriptionPressure: SubscriptionPressureAnalysis;
  confidence: number;
}

export interface BehavioralStabilityAnalysis {
  stabilityLevel: 'stable' | 'shifting' | 'volatile' | 'recovering' | 'unknown';
  consistencyScore: number;
  spendingConsistency: 'unpredictable' | 'variable' | 'consistent' | 'very_consistent' | 'unknown';
  showsRecoverySignals: boolean;
  stabilityObservation: string;
  confidence: number;
}

export interface ReflectionResponse {
  category: 'rhythm' | 'density' | 'structure' | 'stability' | 'temporal' | 'pattern';
  title: string;
  observation: string;
  tone: 'analytical' | 'cautionary' | 'positive' | 'neutral';
  weight: number;
  isSignificant: boolean;
}

export interface FinancialReflection {
  spendingRhythm: SpendingRhythmAnalysis;
  transactionDensity: TransactionDensityAnalysis;
  financialStructure: FinancialStructureAnalysis;
  behavioralStability: BehavioralStabilityAnalysis;
  reflections: ReflectionResponse[];
  dataStatus: 'insufficient_data' | 'partial' | 'ready';
  overallStability: string;
  analysisConfidence: number;
  generatedAt: string;
  fallback: boolean;
}

export const financialReflectionService = {
  async getFinancialReflection(): Promise<FinancialReflection> {
    try {
      const response = await apiClient.get<any>('/v1/financial-reflection/reflection');
      return response.data?.data ?? {
        spendingRhythm: {
          rhythmPattern: 'unknown',
          timeOfDayPreference: 'unknown',
          irregularityScore: 0,
          lateNightTransactionRate: 0,
          hasNightSpendingPattern: false,
          temporalObservation: 'Unable to load reflection analysis.',
          confidence: 0,
        },
        transactionDensity: {
          averageTransactionsPerDay: 0,
          averageTransactionsPerWeek: 0,
          densityLevel: 'unknown',
          hasBurstPatterns: false,
          burstFrequency: 0,
          densityObservation: 'Unable to load reflection analysis.',
          confidence: 0,
        },
        financialStructure: {
          discretionaryRatio: 0,
          recurringRatio: 0,
          structureStability: 'unknown',
          hasConsistentStructure: false,
          structuralObservation: 'Unable to load reflection analysis.',
          subscriptionPressure: {
            pressureScore: 0,
            pressureLevel: 'unknown',
            activeSubscriptionCount: 0,
            pressureObservation: 'Insufficient data.',
          },
          confidence: 0,
        },
        behavioralStability: {
          stabilityLevel: 'unknown',
          consistencyScore: 0,
          spendingConsistency: 'unknown',
          showsRecoverySignals: false,
          stabilityObservation: 'Unable to load reflection analysis.',
          confidence: 0,
        },
        reflections: [],
        dataStatus: 'insufficient_data',
        overallStability: 'unknown',
        analysisConfidence: 0,
        generatedAt: new Date().toISOString(),
        fallback: true,
      };
    } catch (error) {
      console.error('Error fetching financial reflection:', error);
      throw error;
    }
  },
};
