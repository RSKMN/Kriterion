import { ApiResponse, RecurringTransaction, RecurringTransactionRequest } from '@/types'

import { apiClient } from './client'

export const recurringTransactionService = {
  getAll: async () => {
    const response = await apiClient.get<ApiResponse<RecurringTransaction[]>>('/transactions/recurring')
    return response.data
  },

  create: async (data: RecurringTransactionRequest) => {
    const response = await apiClient.post<ApiResponse<RecurringTransaction>>('/transactions/recurring', data)
    return response.data
  },

  update: async (id: string | number, data: RecurringTransactionRequest) => {
    const response = await apiClient.put<ApiResponse<RecurringTransaction>>(`/transactions/recurring/${id}`, data)
    return response.data
  },

  delete: async (id: string | number) => {
    const response = await apiClient.delete<ApiResponse<void>>(`/transactions/recurring/${id}`)
    return response.data
  },
};
