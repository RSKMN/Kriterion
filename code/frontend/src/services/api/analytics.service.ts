import { apiClient } from '@/services/api/client'
import type { ApiResponse, DashboardSummary } from '@/types'

const BASE_URL = '/api/v1/analytics'

export const analyticsService = {
  getDashboardSummary: async (): Promise<ApiResponse<DashboardSummary>> => {
    const { data } = await apiClient.get<ApiResponse<DashboardSummary>>(`${BASE_URL}/dashboard-summary`)
    return data
  },
}