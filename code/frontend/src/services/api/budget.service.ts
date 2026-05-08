import { ApiResponse, Budget, BudgetRequest, BudgetStatus } from '@/types'

import { apiClient } from './client'

const BASE_URL = '/budgets'

export const budgetService = {
  getAll: async (month?: number, year?: number): Promise<ApiResponse<Budget[]>> => {
    const { data } = await apiClient.get<ApiResponse<Budget[]>>(BASE_URL, {
      params: { month, year },
    })
    return data
  },

  getStatus: async (month?: number, year?: number): Promise<ApiResponse<BudgetStatus>> => {
    const { data } = await apiClient.get<ApiResponse<BudgetStatus>>(`${BASE_URL}/status`, {
      params: { month, year },
    })
    return data
  },

  create: async (request: BudgetRequest): Promise<ApiResponse<Budget>> => {
    const { data } = await apiClient.post<ApiResponse<Budget>>(BASE_URL, request)
    return data
  },

  update: async (id: number, request: BudgetRequest): Promise<ApiResponse<Budget>> => {
    const { data } = await apiClient.put<ApiResponse<Budget>>(`${BASE_URL}/${id}`, request)
    return data
  },

  delete: async (id: number): Promise<ApiResponse<void>> => {
    const { data } = await apiClient.delete<ApiResponse<void>>(`${BASE_URL}/${id}`)
    return data
  },
}
