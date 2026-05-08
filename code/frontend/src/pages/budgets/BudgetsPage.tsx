import { Plus, RefreshCcw } from 'lucide-react'
import React, { useEffect, useState, useCallback } from 'react'
import { toast } from 'sonner'

import { BudgetCard } from '@/components/budget/BudgetCard'
import { BudgetForm } from '@/components/budget/BudgetForm'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Dialog, DialogTrigger } from '@/components/ui/dialog'
import { Skeleton } from '@/components/ui/skeleton'
import { budgetService } from '@/services/api'
import { Budget, BudgetStatus } from '@/types'


export default function BudgetsPage() {
  const [status, setStatus] = useState<BudgetStatus | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [isFormOpen, setIsFormOpen] = useState(false)
  const [editingBudget, setEditingBudget] = useState<Budget | null>(null)
  const [isSubmitting, setIsSubmitting] = useState(false)

  const currentMonth = new Date().getMonth() + 1
  const currentYear = new Date().getFullYear()

  const fetchBudgets = useCallback(async () => {
    setLoading(true)
    setError(null)
    try {
      const response = await budgetService.getStatus(currentMonth, currentYear)
      if (response.success) {
        setStatus(response.data)
      } else {
        setError(response.message)
      }
    } catch (err) {
      setError('Failed to load budgets. Please try again later.')
      console.error(err)
    } finally {
      setLoading(false)
    }
  }, [currentMonth, currentYear])

  useEffect(() => {
    void fetchBudgets()
  }, [fetchBudgets])

  const handleCreateOrUpdate = async (data: any) => {
    setIsSubmitting(true)
    try {
      let response
      if (editingBudget) {
        response = await budgetService.update(editingBudget.id, data)
      } else {
        response = await budgetService.create(data)
      }

      if (response.success) {
        toast.success(editingBudget ? 'Budget updated' : 'Budget created')
        setIsFormOpen(false)
        setEditingBudget(null)
        void fetchBudgets()
      } else {
        toast.error(response.message)
      }
    } catch {
      toast.error('An error occurred. Please try again.')
    } finally {
      setIsSubmitting(false)
    }
  }

  const handleDelete = async (id: number) => {
    if (!confirm('Are you sure you want to delete this budget?')) return

    try {
      const response = await budgetService.delete(id)
      if (response.success) {
        toast.success('Budget deleted')
        void fetchBudgets()
      } else {
        toast.error(response.message)
      }
    } catch {
      toast.error('Failed to delete budget')
    }
  }

  const handleEdit = (budget: Budget) => {
    setEditingBudget(budget)
    setIsFormOpen(true)
  }

  return (
    <div className="space-y-8">
      <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
        <section className="space-y-1">
          <p className="text-sm font-medium uppercase tracking-[0.25em] text-muted-foreground">Planning</p>
          <h1 className="text-3xl font-semibold tracking-tight sm:text-4xl">Budgets</h1>
          <p className="text-sm text-muted-foreground sm:text-base">
            Manage your monthly spending limits and stay on track.
          </p>
        </section>

        <Dialog open={isFormOpen} onOpenChange={(open) => {
          setIsFormOpen(open)
          if (!open) setEditingBudget(null)
        }}>
          <DialogTrigger asChild>
            <Button className="w-full sm:w-auto">
              <Plus className="mr-2 h-4 w-4" />
              Add Budget
            </Button>
          </DialogTrigger>
          <BudgetForm 
            initialData={editingBudget}
            onSubmit={handleCreateOrUpdate}
            onCancel={() => setIsFormOpen(false)}
            isSubmitting={isSubmitting}
          />
        </Dialog>
      </div>

      {loading ? (
        <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
          {Array.from({ length: 3 }).map((_, i) => (
            <Skeleton key={i} className="h-[180px] w-full" />
          ))}
        </div>
      ) : error ? (
        <Card className="border-destructive/40 bg-destructive/5">
          <CardHeader>
            <CardTitle className="text-destructive">Error</CardTitle>
            <CardDescription>{error}</CardDescription>
          </CardHeader>
          <CardContent>
            <Button variant="outline" onClick={() => void fetchBudgets()}>
              <RefreshCcw className="mr-2 h-4 w-4" />
              Retry
            </Button>
          </CardContent>
        </Card>
      ) : !status || status.budgetsCount === 0 ? (
        <Card className="flex flex-col items-center justify-center py-12 text-center">
          <CardHeader>
            <CardTitle>No budgets found</CardTitle>
            <CardDescription>
              You haven't set any budgets for {new Date().toLocaleString('default', { month: 'long' })} {currentYear}.
            </CardDescription>
          </CardHeader>
          <CardContent>
            <Button onClick={() => setIsFormOpen(true)}>
              <Plus className="mr-2 h-4 w-4" />
              Create your first budget
            </Button>
          </CardContent>
        </Card>
      ) : (
        <div className="space-y-6">
          <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
             <Card>
               <CardHeader className="pb-2">
                 <CardDescription className="text-xs uppercase tracking-wider font-semibold">Total Budget</CardDescription>
                 <CardTitle className="text-2xl font-bold">{new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(status.totalBudgetLimit)}</CardTitle>
               </CardHeader>
             </Card>
             <Card>
               <CardHeader className="pb-2">
                 <CardDescription className="text-xs uppercase tracking-wider font-semibold">Total Spent</CardDescription>
                 <CardTitle className="text-2xl font-bold">{new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(status.totalSpent)}</CardTitle>
               </CardHeader>
             </Card>
             <Card>
               <CardHeader className="pb-2">
                 <CardDescription className="text-xs uppercase tracking-wider font-semibold">Warnings</CardDescription>
                 <CardTitle className={status.warningBudgetsCount > 0 ? "text-2xl font-bold text-amber-600" : "text-2xl font-bold"}>{status.warningBudgetsCount}</CardTitle>
               </CardHeader>
             </Card>
             <Card>
               <CardHeader className="pb-2">
                 <CardDescription className="text-xs uppercase tracking-wider font-semibold">Exceeded</CardDescription>
                 <CardTitle className={status.exceededBudgetsCount > 0 ? "text-2xl font-bold text-rose-600" : "text-2xl font-bold"}>{status.exceededBudgetsCount}</CardTitle>
               </CardHeader>
             </Card>
          </div>

          <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
            {status.budgets.map((budget) => (
              <BudgetCard 
                key={budget.id} 
                budget={budget} 
                onEdit={handleEdit}
                onDelete={handleDelete}
              />
            ))}
          </div>
        </div>
      )}
    </div>
  )
}
