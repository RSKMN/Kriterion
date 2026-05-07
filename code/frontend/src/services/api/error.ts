import type { AxiosError } from 'axios'

import type { ApiErrorResponse } from '@/types'

export function getApiErrorMessage(error: unknown): string {
  const axiosError = error as AxiosError<ApiErrorResponse>
  return axiosError.response?.data?.message ?? 'Something went wrong'
}
