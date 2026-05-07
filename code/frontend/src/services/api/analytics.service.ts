import { apiClient } from '@/services/api/client'
import type { ApiResponse, DashboardSummary } from '@/types'

const BASE_URL = '/api/v1/analytics'

export const analyticsService = {
  getDashboardSummary: async (): Promise<ApiResponse<DashboardSummary>> => {
    const { data } = await apiClient.get<ApiResponse<DashboardSummary>>(`${BASE_URL}/dashboard-summary`)

    // compute derived fields if backend doesn't provide them
    if (data?.data) {
      const summary = data.data

      // totalBalance: prefer explicit field, otherwise fallback to remainingBalance
      if (summary.totalBalance === undefined) {
        summary.totalBalance = summary.remainingBalance
      }

      // savings: prefer explicit, otherwise income - expense
      if (summary.savings === undefined) {
        summary.savings = Number((summary.totalIncome - summary.totalExpense).toFixed(2))
      }

      // compute monthly savings
      if (Array.isArray(summary.monthlySummary)) {
        summary.monthlySummary = summary.monthlySummary.map((m) => ({
          ...m,
          savings: m.savings === undefined ? Number((m.totalIncome - m.totalExpense).toFixed(2)) : m.savings,
        }))
      }
    }

    return data
  },
  getMonthlyTrends: async (): Promise<ApiResponse<import('@/types').MonthlyTrendItem[]>> => {
    const { data } = await apiClient.get<ApiResponse<import('@/types').MonthlyTrendItem[]>>(
      `${BASE_URL}/monthly-trends`,
    )
    return data
  },
  getCategoryBreakdown: async (): Promise<ApiResponse<import('@/types').CategoryBreakdownItem[]>> => {
    const { data } = await apiClient.get<ApiResponse<import('@/types').CategoryBreakdownItem[]>>(
      `${BASE_URL}/category-breakdown`,
    )
    return data
  },
  getWeeklyInsights: async (): Promise<ApiResponse<import('@/types').WeeklyInsight[]>> => {
    const { data } = await apiClient.get<ApiResponse<import('@/types').WeeklyInsight[]>>(
      `${BASE_URL}/weekly-insights`,
    )
    return data
  },
}