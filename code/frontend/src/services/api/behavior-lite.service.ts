import { apiClient } from './client'

import { BehaviorLiteSummary, PredictLiteForecast } from '@/types/behavior-lite'

export const behaviorLiteService = {
  getSummary: async (): Promise<BehaviorLiteSummary> => {
    const response = await apiClient.get('/v1/behavior-lite/summary')
    return response.data.data
  },

  getForecast: async (): Promise<PredictLiteForecast> => {
    const response = await apiClient.get('/v1/predict-lite/forecast')
    return response.data.data
  },

  seedDemoData: async (): Promise<boolean> => {
    const response = await apiClient.post('/v1/behavior-lite/seed')
    return response.data?.data === 'seeded'
  },
}
