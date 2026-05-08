import { zodResolver } from '@hookform/resolvers/zod'
import React, { useEffect } from 'react'
import { useForm } from 'react-hook-form'
import * as z from 'zod'

import { CategoryDropdown } from '@/components/CategoryDropdown'
import { Button } from '@/components/ui/button'
import { 
  DialogContent, 
  DialogHeader, 
  DialogTitle, 
  DialogFooter,
  DialogDescription
} from '@/components/ui/dialog'
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from '@/components/ui/form'
import { Input } from '@/components/ui/input'
import { BudgetRequest, Budget } from '@/types'

const budgetSchema = z.object({
  categoryId: z.union([z.number(), z.string(), z.null()]).optional(),
  monthlyLimit: z.coerce.number().positive('Limit must be greater than 0'),
  month: z.number().min(1).max(12),
  year: z.number().min(2000),
})

type BudgetFormValues = z.infer<typeof budgetSchema>

interface BudgetFormProps {
  initialData?: Budget | null
  onSubmit: (data: BudgetRequest) => void
  onCancel: () => void
  isSubmitting?: boolean
}

export function BudgetForm({ initialData, onSubmit, onCancel, isSubmitting }: BudgetFormProps) {
  const form = useForm<BudgetFormValues>({
    resolver: zodResolver(budgetSchema),
    defaultValues: {
      categoryId: null,
      monthlyLimit: 0,
      month: new Date().getMonth() + 1,
      year: new Date().getFullYear(),
    },
  })

  useEffect(() => {
    if (initialData) {
      form.reset({
        categoryId: initialData.categoryId,
        monthlyLimit: initialData.monthlyLimit,
        month: initialData.month,
        year: initialData.year,
      })
    }
  }, [initialData, form])

  const handleFormSubmit = (values: BudgetFormValues) => {
    onSubmit({
      categoryId: values.categoryId === 'overall' || values.categoryId === '' ? null : Number(values.categoryId),
      monthlyLimit: values.monthlyLimit,
      month: values.month,
      year: values.year,
    })
  }

  return (
    <DialogContent className="sm:max-w-[425px]">
      <DialogHeader>
        <DialogTitle>{initialData ? 'Edit Budget' : 'Create Budget'}</DialogTitle>
        <DialogDescription>
          Set a monthly spending limit for a category or your overall expenses.
        </DialogDescription>
      </DialogHeader>

      <Form {...form}>
        <form onSubmit={form.handleSubmit(handleFormSubmit)} className="space-y-4 py-4">
          <FormField
            control={form.control}
            name="categoryId"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Category</FormLabel>
                <FormControl>
                  <CategoryDropdown
                    value={(field.value ?? 'overall').toString()}
                    onChange={(val) => field.onChange(val === 'overall' ? null : Number(val))}
                    placeholder="Overall Budget"
                    showOverallOption
                  />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />

          <FormField
            control={form.control}
            name="monthlyLimit"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Monthly Limit ($)</FormLabel>
                <FormControl>
                  <Input 
                    type="number" 
                    step="0.01" 
                    placeholder="0.00" 
                    {...field} 
                  />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />

          <div className="grid grid-cols-2 gap-4">
            <FormField
              control={form.control}
              name="month"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Month</FormLabel>
                  <FormControl>
                    <Input type="number" min={1} max={12} {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="year"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Year</FormLabel>
                  <FormControl>
                    <Input type="number" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
          </div>

          <DialogFooter className="pt-4">
            <Button type="button" variant="outline" onClick={onCancel}>
              Cancel
            </Button>
            <Button type="submit" disabled={isSubmitting}>
              {initialData ? 'Save Changes' : 'Create Budget'}
            </Button>
          </DialogFooter>
        </form>
      </Form>
    </DialogContent>
  )
}
