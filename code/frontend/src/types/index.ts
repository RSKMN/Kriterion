export type ID = string | number

export interface ApiErrorResponse {
  message: string
  statusCode?: number
}

export interface AuthUser {
  id: ID
  fullName: string
  email: string
}

export interface ApiResponse<T> {
  success: boolean
  message: string
  data: T
}

export interface LoginRequest {
  email: string
  password: string
}

export interface RegisterRequest {
  fullName: string
  email: string
  password: string
}

export interface RefreshTokenRequest {
  refreshToken: string
}

export interface PageableResponse<T> {
  content: T[]
  pageable: {
    pageNumber: number
    pageSize: number
  }
  totalPages: number
  totalElements: number
  last: boolean
  first: boolean
  size: number
  number: number
  numberOfElements: number
  empty: boolean
}

export * from './category'
export * from './transaction'
