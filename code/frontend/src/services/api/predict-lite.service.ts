import { apiClient } from './client';
import { PredictiveForecast } from '@/types/predict-lite';

export const predictLiteService = {
  async getForecast(): Promise<PredictiveForecast> {
    const response = await apiClient.get('/v1/predictive-analytics/forecast');
    return response.data?.data;
  },

  async seedDemoData(): Promise<boolean> {
    const response = await apiClient.post('/v1/predictive-analytics/seed');
    return response.data?.data === 'seeded';
  },
};
