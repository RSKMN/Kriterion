import { Plus, RefreshCcw } from 'lucide-react'
import React, { useEffect, useState, useCallback } from 'react'
import { toast } from 'sonner'

import { RecurringTransactionForm } from '@/components/forms/RecurringTransactionForm'
import { RecurringTransactionTable } from '@/components/recurring/RecurringTransactionTable'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle, DialogTrigger } from '@/components/ui/dialog'
import { Skeleton } from '@/components/ui/skeleton'
import { recurringTransactionService } from '@/services/api'
import { RecurringTransaction, RecurringTransactionRequest } from '@/types'

export default function RecurringTransactionsPage() {
  const [data, setData] = useState<RecurringTransaction[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [isFormOpen, setIsFormOpen] = useState(false)
  const [editingRT, setEditingRT] = useState<RecurringTransaction | null>(null)
  const [isSubmitting, setIsSubmitting] = useState(false)

  const fetchData = useCallback(async () => {
    setLoading(true)
    setError(null)
    try {
      const response = await recurringTransactionService.getAll()
      if (response.success) {
        setData(response.data)
      } else {
        setError(response.message)
      }
    } catch (err) {
      setError('Failed to load recurring transactions. Please try again.')
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    void fetchData()
  }, [fetchData])

  const handleCreateOrUpdate = async (formData: RecurringTransactionRequest) => {
    setIsSubmitting(true)
    try {
      let response
      if (editingRT) {
        response = await recurringTransactionService.update(editingRT.id, formData)
      } else {
        response = await recurringTransactionService.create(formData)
      }

      if (response.success) {
        toast.success(editingRT ? 'Schedule updated' : 'Schedule created')
        setIsFormOpen(false)
        setEditingRT(null)
        void fetchData()
      } else {
        toast.error(response.message)
      }
    } catch (err) {
      toast.error('An error occurred. Please try again.')
    } finally {
      setIsSubmitting(false)
    }
  }

  const handleDelete = async (id: string | number) => {
    if (!confirm('Are you sure you want to delete this schedule? This will not delete previously generated transactions.')) return

    try {
      const response = await recurringTransactionService.delete(id)
      if (response.success) {
        toast.success('Schedule deleted')
        void fetchData()
      } else {
        toast.error(response.message)
      }
    } catch (err) {
      toast.error('Failed to delete schedule')
    }
  }

  const handleEdit = (rt: RecurringTransaction) => {
    setEditingRT(rt)
    setIsFormOpen(true)
  }

  return (
    <div className="space-y-8">
      <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
        <section className="space-y-1">
          <p className="text-sm font-medium uppercase tracking-[0.25em] text-muted-foreground">Automation</p>
          <h1 className="text-3xl font-semibold tracking-tight sm:text-4xl">Recurring</h1>
          <p className="text-sm text-muted-foreground sm:text-base">
            Set up automatic income and expense schedules.
          </p>
        </section>

        <Dialog open={isFormOpen} onOpenChange={(open) => {
          setIsFormOpen(open)
          if (!open) setEditingRT(null)
        }}>
          <DialogTrigger asChild>
            <Button className="w-full sm:w-auto">
              <Plus className="mr-2 h-4 w-4" />
              Add Schedule
            </Button>
          </DialogTrigger>
          <DialogContent className="sm:max-w-[500px]">
            <DialogHeader>
              <DialogTitle>{editingRT ? 'Edit Schedule' : 'New Recurring Transaction'}</DialogTitle>
              <DialogDescription>
                Fill in the details for your automatic transaction schedule.
              </DialogDescription>
            </DialogHeader>
            <RecurringTransactionForm
              initialData={editingRT}
              onSubmit={handleCreateOrUpdate}
              onCancel={() => setIsFormOpen(false)}
              isSubmitting={isSubmitting}
            />
          </DialogContent>
        </Dialog>
      </div>

      {loading ? (
        <div className="space-y-4">
          <Skeleton className="h-[400px] w-full" />
        </div>
      ) : error ? (
        <Card className="border-destructive/40 bg-destructive/5">
          <CardHeader>
            <CardTitle className="text-destructive">Error</CardTitle>
            <CardDescription>{error}</CardDescription>
          </CardHeader>
          <CardContent>
            <Button variant="outline" onClick={() => void fetchData()}>
              <RefreshCcw className="mr-2 h-4 w-4" />
              Retry
            </Button>
          </CardContent>
        </Card>
      ) : (
        <RecurringTransactionTable 
          data={data} 
          onEdit={handleEdit}
          onDelete={handleDelete}
        />
      )}
    </div>
  )
}
