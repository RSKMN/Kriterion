export interface MonthlySummary {
  month: string
  totalIncome: number
  totalExpense: number
  remainingBalance: number
  // optional derived value: month savings (income - expense)
  savings?: number
}

export interface DashboardSummary {
  totalIncome: number
  totalExpense: number
  remainingBalance: number
  // optional derived fields
  totalBalance?: number
  savings?: number
  monthlySummary: MonthlySummary[]
}
 
export interface MonthlyTrendItem {
  month: string
  totalIncome: number
  totalExpense: number
  remainingBalance: number
}

export interface CategoryBreakdownItem {
  category: string
  total: number
}

export interface TopCategoryItem {
  category: string
  total: number
  percentage?: number
}

export interface WeeklyInsight {
  period: string
  totalIncome: number
  totalExpense: number
  percentChangeExpense?: number
}