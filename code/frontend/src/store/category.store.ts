import { create } from 'zustand'

import { categoryService } from '@/services/api/category.service'
import { Category, CreateCategoryRequest, UpdateCategoryRequest } from '@/types'

interface CategoryState {
  categories: Category[]
  loading: boolean
  error: string | null
  fetchCategories: () => Promise<void>
  createCategory: (data: CreateCategoryRequest) => Promise<void>
  updateCategory: (id: string | number, data: UpdateCategoryRequest) => Promise<void>
  deleteCategory: (id: string | number) => Promise<void>
}

export const useCategoryStore = create<CategoryState>((set, get) => ({
  categories: [],
  loading: false,
  error: null,

  fetchCategories: async () => {
    set({ loading: true, error: null })
    try {
      const response = await categoryService.getAll()
      if (response.success) {
        set({ categories: response.data, loading: false })
      } else {
        set({ error: response.message, loading: false })
      }
    } catch (error: any) {
      set({ error: error.message || 'Failed to fetch categories', loading: false })
    }
  },

  createCategory: async (data) => {
    set({ loading: true, error: null })
    try {
      const response = await categoryService.create(data)
      if (response.success) {
        set((state) => ({ categories: [...state.categories, response.data], loading: false }))
      } else {
        set({ error: response.message, loading: false })
        throw new Error(response.message)
      }
    } catch (error: any) {
      const errorMessage = error?.response?.data?.message || error.message || 'Failed to create category'
      set({ error: errorMessage, loading: false })
      throw new Error(errorMessage)
    }
  },

  updateCategory: async (id, data) => {
    set({ loading: true, error: null })
    try {
      const response = await categoryService.update(id, data)
      if (response.success) {
        set((state) => ({
          categories: state.categories.map((c) => (c.id === id ? response.data : c)),
          loading: false,
        }))
      } else {
        set({ error: response.message, loading: false })
        throw new Error(response.message)
      }
    } catch (error: any) {
      const errorMessage = error?.response?.data?.message || error.message || 'Failed to update category'
      set({ error: errorMessage, loading: false })
      throw new Error(errorMessage)
    }
  },

  deleteCategory: async (id) => {
    set({ loading: true, error: null })
    try {
      const response = await categoryService.delete(id)
      if (response.success) {
        set((state) => ({
          categories: state.categories.filter((c) => c.id !== id),
          loading: false,
        }))
      } else {
        set({ error: response.message, loading: false })
        throw new Error(response.message)
      }
    } catch (error: any) {
      const errorMessage = error?.response?.data?.message || error.message || 'Failed to delete category'
      set({ error: errorMessage, loading: false })
      throw new Error(errorMessage)
    }
  },
}))
