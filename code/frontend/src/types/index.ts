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
