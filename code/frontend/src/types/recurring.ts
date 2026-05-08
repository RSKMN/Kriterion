import { PaymentMethod } from './transaction'

import { ID, CategoryType } from './index'

export type RecurrenceType = 'DAILY' | 'WEEKLY' | 'MONTHLY' | 'YEARLY'

export interface RecurringTransaction {
  id: ID
  title: string
  amount: number
  categoryId: ID
  categoryName: string
  type: CategoryType
  recurrenceType: RecurrenceType
  paymentMethod: PaymentMethod
  startDate: string
  endDate?: string
  nextRunDate: string
  reminderDaysBefore?: number
  isActive: boolean
}

export interface RecurringTransactionRequest {
  title: string
  amount: number
  categoryId: ID
  type: CategoryType
  recurrenceType: RecurrenceType
  paymentMethod?: PaymentMethod
  startDate: string
  endDate?: string
  reminderDaysBefore?: number
  isActive?: boolean
}
