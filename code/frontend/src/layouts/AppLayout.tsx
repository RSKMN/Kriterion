import { Outlet } from 'react-router-dom'

import { DashboardShell } from '@/layouts/DashboardShell'

export function AppLayout() {
  return (
    <DashboardShell>
      <Outlet />
    </DashboardShell>
  )
}
