import { NavLink } from 'react-router-dom'

const items = [
  { to: '/dashboard', label: 'Dashboard' },
  { to: '/transactions', label: 'Transactions' },
  { to: '/receipts', label: 'Receipt Scanner' },
  { to: '/budgets', label: 'Budgets' },
  { to: '/recurring', label: 'Recurring' },
  { to: '/analytics', label: 'Analytics' },
  { to: '/behavior-lite', label: 'Behavioral Insights Lite' },
  { to: '/predict-lite', label: 'Predictive Analytics Lite' },
  { to: '/financial-reflection', label: 'Financial Reflection' },
  { to: '/reports', label: 'Reports' },
  { to: '/settings', label: 'Settings' },
]

export function Sidebar() {
  return (
    <aside className="hidden w-64 border-r bg-card p-4 md:block">
      <div className="mb-6 text-lg font-semibold">Kriterion</div>
      <nav className="space-y-1">
        {items.map((item) => (
          <NavLink key={item.to} to={item.to} className={({ isActive }) => (isActive ? 'block rounded-md bg-primary px-3 py-2 text-primary-foreground' : 'block rounded-md px-3 py-2 text-muted-foreground')}>
            {item.label}
          </NavLink>
        ))}
      </nav>
    </aside>
  )
}
