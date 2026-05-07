import { Category, CreateCategoryRequest, UpdateCategoryRequest } from '@/types'
import { ApiResponse } from '@/types'

import { apiClient } from './client'

const BASE_URL = '/api/v1/categories'

export const categoryService = {
  getAll: async (): Promise<ApiResponse<Category[]>> => {
    const { data } = await apiClient.get<ApiResponse<Category[]>>(BASE_URL)
    return data
  },

  create: async (payload: CreateCategoryRequest): Promise<ApiResponse<Category>> => {
    const { data } = await apiClient.post<ApiResponse<Category>>(BASE_URL, payload)
    return data
  },

  update: async (id: string | number, payload: UpdateCategoryRequest): Promise<ApiResponse<Category>> => {
    const { data } = await apiClient.put<ApiResponse<Category>>(`${BASE_URL}/${id}`, payload)
    return data
  },

  delete: async (id: string | number): Promise<ApiResponse<string>> => {
    const { data } = await apiClient.delete<ApiResponse<string>>(`${BASE_URL}/${id}`)
    return data
  },
}
