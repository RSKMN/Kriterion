import { useCallback, useEffect, useState } from 'react'

import { analyticsService } from '@/services/api'
import { getApiErrorMessage } from '@/services/api/error'
import type { DashboardSummary } from '@/types'

export function useDashboardSummary() {
  const [summary, setSummary] = useState<DashboardSummary | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  const load = useCallback(async () => {
    setLoading(true)
    setError(null)

    try {
      const response = await analyticsService.getDashboardSummary()
      if (response.success) {
        setSummary(response.data)
      } else {
        setError(response.message)
      }
    } catch (requestError) {
      setError(getApiErrorMessage(requestError))
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    void load()
  }, [load])

  return { summary, loading, error, reload: load }
}
