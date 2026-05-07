import { ID } from './index'

export enum CategoryType {
  INCOME = 'INCOME',
  EXPENSE = 'EXPENSE',
}

export interface Category {
  id: ID
  name: string
  type: CategoryType
  icon?: string
  color?: string
  isDefault: boolean
  createdAt: string
}

export interface CreateCategoryRequest {
  name: string
  type: CategoryType
  icon?: string
  color?: string
}

export interface UpdateCategoryRequest {
  name: string
  icon?: string
  color?: string
}
