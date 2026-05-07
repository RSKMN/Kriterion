import { apiClient } from '@/services/api/client'

export const authService = {
  login: (payload: unknown) => apiClient.post('/auth/login', payload),
  register: (payload: unknown) => apiClient.post('/auth/register', payload),
  me: () => apiClient.get('/auth/me'),
}
