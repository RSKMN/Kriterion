import { apiClient } from './client';

export const exportService = {
  exportTransactionsCsv: async (params?: {
    categoryId?: number;
    type?: string;
    startDate?: string;
    endDate?: string;
    search?: string;
  }) => {
    const response = await apiClient.get('/exports/transactions/csv', {
      params,
      responseType: 'blob',
    });
    return response.data;
  },

  downloadMonthlyReport: async () => {
    const response = await apiClient.get('/exports/reports/monthly', {
      responseType: 'blob',
    });
    return response.data;
  },

  downloadYearlyReport: async () => {
    const response = await apiClient.get('/exports/reports/yearly', {
      responseType: 'blob',
    });
    return response.data;
  }
};
