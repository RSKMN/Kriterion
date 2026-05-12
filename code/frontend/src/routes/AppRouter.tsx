import { Suspense, lazy } from 'react'
import { Navigate, Route, Routes } from 'react-router-dom'

import { PageLoader } from '@/components/feedback/PageLoader'
import { AppLayout } from '@/layouts/AppLayout'
import { ProtectedRoute } from '@/routes/ProtectedRoute'

const LandingPage = lazy(() => import('@/pages/LandingPage'))
const LoginPage = lazy(() => import('@/pages/auth/LoginPage'))
const RegisterPage = lazy(() => import('@/pages/auth/RegisterPage'))
const DashboardPage = lazy(() => import('@/pages/dashboard/DashboardPage'))
const TransactionsPage = lazy(() => import('@/pages/transactions/TransactionsPage'))
const BudgetsPage = lazy(() => import('@/pages/budgets/BudgetsPage'))
const RecurringTransactionsPage = lazy(() => import('@/pages/recurring/RecurringTransactionsPage'))
const AnalyticsPage = lazy(() => import('@/pages/analytics/AnalyticsPage'))
const SettingsPage = lazy(() => import('@/pages/settings/SettingsPage'))
const ReceiptScannerPage = lazy(() => import('@/pages/receipts/ReceiptScannerPage'))
const NotificationsPage = lazy(() => import('@/pages/NotificationsPage'))
const ReportsPage = lazy(() => import('@/pages/ReportsPage'))
const BehavioralInsightsLitePage = lazy(() => import('@/pages/behavior-lite/BehavioralInsightsLitePage'))
const PredictiveAnalyticsLitePage = lazy(() => import('@/pages/predict-lite/PredictiveAnalyticsLitePage'))
const FinancialReflectionPage = lazy(() => import('@/pages/financial-reflection/FinancialReflectionPage'))

export function AppRouter() {
  return (
    <Suspense fallback={<PageLoader />}>
      <Routes>
        <Route path="/" element={<LandingPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route
          element={
            <ProtectedRoute>
              <AppLayout />
            </ProtectedRoute>
          }
        >
          <Route path="/dashboard" element={<DashboardPage />} />
          <Route path="/transactions" element={<TransactionsPage />} />
          <Route path="/budgets" element={<BudgetsPage />} />
          <Route path="/recurring" element={<RecurringTransactionsPage />} />
          <Route path="/analytics" element={<AnalyticsPage />} />
          <Route path="/settings" element={<SettingsPage />} />
          <Route path="/receipts" element={<ReceiptScannerPage />} />
          <Route path="/notifications" element={<NotificationsPage />} />
          <Route path="/reports" element={<ReportsPage />} />
          <Route path="/behavior-lite" element={<BehavioralInsightsLitePage />} />
          <Route path="/predict-lite" element={<PredictiveAnalyticsLitePage />} />
          <Route path="/financial-reflection" element={<FinancialReflectionPage />} />
        </Route>
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </Suspense>
  )
}
