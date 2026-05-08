import { apiClient } from '@/services/api/client'
import type { ApiResponse, DashboardSummary } from '@/types'

const BASE_URL = '/analytics'

export const analyticsService = {
  getDashboardSummary: async (): Promise<ApiResponse<DashboardSummary>> => {
    const { data } = await apiClient.get<ApiResponse<any>>(`${BASE_URL}/dashboard-summary`)

    if (data?.data) {
      const b = data.data
      const summary: DashboardSummary = {
        totalIncome: b.totalIncome,
        totalExpense: b.totalExpense,
        remainingBalance: b.remainingBalance,
        totalBalance: b.totalBalance ?? b.remainingBalance,
        savings: b.savings ?? Number((b.totalIncome - b.totalExpense).toFixed(2)),
        monthlySummary: (b.monthlySummary || []).map((m: any) => ({
          month: m.monthKey,
          totalIncome: m.totalIncome,
          totalExpense: m.totalExpense,
          remainingBalance: m.balance,
          savings: Number((m.totalIncome - m.totalExpense).toFixed(2)),
        })),
      }
      return { ...data, data: summary }
    }

    return data
  },

  getMonthlyTrends: async (): Promise<ApiResponse<import('@/types').MonthlyTrendItem[]>> => {
    const { data } = await apiClient.get<ApiResponse<any>>(`${BASE_URL}/monthly-trends`)
    if (data.success && data.data?.trends) {
      const trends = data.data.trends.map((t: any) => ({
        month: t.monthKey,
        totalIncome: t.totalIncome,
        totalExpense: t.totalExpense,
        remainingBalance: t.balance,
      }))
      return { ...data, data: trends }
    }
    return data
  },

  getCategoryBreakdown: async (): Promise<ApiResponse<import('@/types').CategoryBreakdownItem[]>> => {
    const { data } = await apiClient.get<ApiResponse<any>>(`${BASE_URL}/category-breakdown`)
    if (data.success && data.data?.categories) {
      const categories = data.data.categories.map((c: any) => ({
        category: c.categoryName,
        total: c.totalSpent,
      }))
      return { ...data, data: categories }
    }
    return data
  },

  getWeeklyInsights: async (): Promise<ApiResponse<import('@/types').WeeklyInsight[]>> => {
    const { data } = await apiClient.get<ApiResponse<any>>(`${BASE_URL}/weekly-insights`)
    if (data.success && data.data?.insights) {
      const insights = data.data.insights.map((i: any) => ({
        period: i.dayLabel,
        totalIncome: i.totalIncome,
        totalExpense: i.totalExpense,
      }))
      return { ...data, data: insights }
    }
    return data
  },
}