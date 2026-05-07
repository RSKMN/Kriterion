export interface MonthlySummary {
  month: string
  totalIncome: number
  totalExpense: number
  remainingBalance: number
}

export interface DashboardSummary {
  totalIncome: number
  totalExpense: number
  remainingBalance: number
  monthlySummary: MonthlySummary[]
}