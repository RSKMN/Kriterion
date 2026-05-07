import {
  Transaction,
  CreateTransactionRequest,
  UpdateTransactionRequest,
  PageableResponse,
} from '@/types'
import { ApiResponse } from '@/types'

import { apiClient } from './client'

const BASE_URL = '/api/v1/transactions'

interface GetTransactionsParams {
  categoryId?: number | string
  startDate?: string
  endDate?: string
  search?: string
  page?: number
  size?: number
  sortBy?: string
  sortDir?: 'asc' | 'desc'
}

export const transactionService = {
  getAll: async (params?: GetTransactionsParams): Promise<ApiResponse<PageableResponse<Transaction>>> => {
    const { data } = await apiClient.get<ApiResponse<PageableResponse<Transaction>>>(BASE_URL, {
      params,
    })
    return data
  },

  create: async (payload: CreateTransactionRequest): Promise<ApiResponse<Transaction>> => {
    const { data } = await apiClient.post<ApiResponse<Transaction>>(BASE_URL, payload)
    return data
  },

  update: async (id: string | number, payload: UpdateTransactionRequest): Promise<ApiResponse<Transaction>> => {
    const { data } = await apiClient.put<ApiResponse<Transaction>>(`${BASE_URL}/${id}`, payload)
    return data
  },

  delete: async (id: string | number): Promise<ApiResponse<string>> => {
    const { data } = await apiClient.delete<ApiResponse<string>>(`${BASE_URL}/${id}`)
    return data
  },
}
