import { create } from 'zustand'

import { transactionService } from '@/services/api/transaction.service'
import {
  Transaction,
  CreateTransactionRequest,
  UpdateTransactionRequest,
} from '@/types'

interface PaginationState {
  page: number
  size: number
  totalElements: number
  totalPages: number
}

interface TransactionState {
  transactions: Transaction[]
  loading: boolean
  error: string | null
  pagination: PaginationState
  fetchTransactions: (params?: {
    categoryId?: string | number
    type?: string
    startDate?: string
    endDate?: string
    search?: string
    page?: number
    size?: number
  }) => Promise<void>
  createTransaction: (data: CreateTransactionRequest) => Promise<void>
  updateTransaction: (id: string | number, data: UpdateTransactionRequest) => Promise<void>
  deleteTransaction: (id: string | number) => Promise<void>
}

export const useTransactionStore = create<TransactionState>((set, get) => ({
  transactions: [],
  loading: false,
  error: null,
  pagination: {
    page: 0,
    size: 20,
    totalElements: 0,
    totalPages: 0,
  },

  fetchTransactions: async (params) => {
    set({ loading: true, error: null })
    try {
      const response = await transactionService.getAll({
        page: get().pagination.page,
        size: get().pagination.size,
        ...params,
      })
      if (response.success) {
        set({
          transactions: response.data.content,
          pagination: {
            page: response.data.number,
            size: response.data.size,
            totalElements: response.data.totalElements,
            totalPages: response.data.totalPages,
          },
          loading: false,
        })
      } else {
        set({ error: response.message, loading: false })
      }
    } catch (error: any) {
      set({ error: error.message || 'Failed to fetch transactions', loading: false })
    }
  },

  createTransaction: async (data) => {
    set({ loading: true, error: null })
    try {
      const response = await transactionService.create(data)
      if (response.success) {
        // Refresh the list to apply sorting/pagination correctly
        await get().fetchTransactions()
      } else {
        set({ error: response.message, loading: false })
        throw new Error(response.message)
      }
    } catch (error: any) {
      const errorMessage = error?.response?.data?.message || error.message || 'Failed to create transaction'
      set({ error: errorMessage, loading: false })
      throw new Error(errorMessage)
    }
  },

  updateTransaction: async (id, data) => {
    set({ loading: true, error: null })
    try {
      const response = await transactionService.update(id, data)
      if (response.success) {
        set((state) => ({
          transactions: state.transactions.map((t) => (t.id === id ? response.data : t)),
          loading: false,
        }))
      } else {
        set({ error: response.message, loading: false })
        throw new Error(response.message)
      }
    } catch (error: any) {
      const errorMessage = error?.response?.data?.message || error.message || 'Failed to update transaction'
      set({ error: errorMessage, loading: false })
      throw new Error(errorMessage)
    }
  },

  deleteTransaction: async (id) => {
    set({ loading: true, error: null })
    try {
      const response = await transactionService.delete(id)
      if (response.success) {
        set((state) => ({
          transactions: state.transactions.filter((t) => t.id !== id),
          pagination: {
            ...state.pagination,
            totalElements: Math.max(0, state.pagination.totalElements - 1),
          },
          loading: false,
        }))
      } else {
        set({ error: response.message, loading: false })
        throw new Error(response.message)
      }
    } catch (error: any) {
      const errorMessage = error?.response?.data?.message || error.message || 'Failed to delete transaction'
      set({ error: errorMessage, loading: false })
      throw new Error(errorMessage)
    }
  },
}))
