export interface Budget {
  id: number
  categoryId: number | null
  categoryName: string
  monthlyLimit: number
  currentSpent: number
  remaining: number
  percentage: number
  month: number
  year: number
  alertThreshold: number
  isWarning: boolean
  isExceeded: boolean
}

export interface BudgetRequest {
  categoryId?: number | null
  monthlyLimit: number
  month: number
  year: number
}

export interface BudgetStatus {
  totalBudgetLimit: number
  totalSpent: number
  totalRemaining: number
  totalPercentage: number
  budgetsCount: number
  warningBudgetsCount: number
  exceededBudgetsCount: number
  budgets: Budget[]
}
