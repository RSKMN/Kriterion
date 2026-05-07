import axios from 'axios'

import { API_BASE_URL } from '@/constants'
import { useAuthStore } from '@/store/auth.store'

export const apiClient = axios.create({
  baseURL: API_BASE_URL,
  withCredentials: true,
})

// Request interceptor: attach JWT to Authorization header
apiClient.interceptors.request.use((config) => {
  const token = useAuthStore.getState().token
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// Response interceptor: handle unauthorized responses
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Token invalid/expired, clear auth and redirect will be handled by ProtectedRoute
      useAuthStore.getState().clearAuth()
      window.location.href = '/login'
    }
    return Promise.reject(error)
  },
)
