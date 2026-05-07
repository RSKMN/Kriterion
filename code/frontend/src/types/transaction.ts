import { ID, CategoryType } from './index'

export enum PaymentMethod {
  CASH = 'CASH',
  CREDIT_CARD = 'CREDIT_CARD',
  DEBIT_CARD = 'DEBIT_CARD',
  BANK_TRANSFER = 'BANK_TRANSFER',
  PAYPAL = 'PAYPAL',
  CRYPTO = 'CRYPTO',
  OTHER = 'OTHER',
}

export interface Transaction {
  id: ID
  userId: ID
  categoryId: ID
  categoryName: string
  categoryColor?: string
  type: CategoryType
  amount: number
  title: string
  description?: string
  paymentMethod?: PaymentMethod
  transactionDate: string
  isRecurring: boolean
  recurringTransactionId?: ID
  receiptId?: ID
  aiCategorized: boolean
  location?: string
  merchantName?: string
  createdAt: string
  updatedAt: string
}

export interface CreateTransactionRequest {
  title: string
  amount: number
  type: CategoryType
  categoryId: ID
  transactionDate: string
  paymentMethod?: PaymentMethod
  merchantName?: string
  description?: string
  location?: string
  isRecurring?: boolean
}

export interface UpdateTransactionRequest extends CreateTransactionRequest {}
