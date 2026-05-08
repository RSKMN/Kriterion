import { zodResolver } from '@hookform/resolvers/zod'
import { Info, Sparkles } from 'lucide-react'
import { useEffect, useMemo } from 'react'
import { useForm } from 'react-hook-form'
import * as z from 'zod'

import { CategoryDropdown } from '@/components/CategoryDropdown'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from '@/components/ui/form'
import { Input } from '@/components/ui/input'
import { cn } from '@/lib/utils'
import { useCategoryStore } from '@/store/category.store'
import { CategoryType, PaymentMethod, CreateTransactionRequest } from '@/types'
import { ExtractedData } from '@/utils/ocr-parser'



const formSchema = z.object({
  title: z.string().min(1, 'Title is required').max(100),
  amount: z.coerce.number().positive('Amount must be greater than 0'),
  type: z.nativeEnum(CategoryType),
  categoryId: z.string().min(1, 'Category is required'),
  transactionDate: z.string().min(1, 'Date is required'),
  paymentMethod: z.nativeEnum(PaymentMethod),
  merchantName: z.string().max(100).optional().or(z.literal('')),
})

type FormValues = z.infer<typeof formSchema>

interface ExtractedDataFormProps {
  data: ExtractedData
  onSubmit: (data: CreateTransactionRequest) => Promise<void>
  onCancel: () => void
  loading?: boolean
}

export function ExtractedDataForm({ data, onSubmit, onCancel, loading }: ExtractedDataFormProps) {
  const { categories, fetchCategories } = useCategoryStore()

  useEffect(() => {
    if (categories.length === 0) {
      fetchCategories()
    }
  }, [categories.length, fetchCategories])

  // Try to find the suggested category ID from the store
  const suggestedCategoryId = useMemo(() => {
    const suggestedName = data.suggestion.categoryName.toLowerCase()
    const match = categories.find(c => 
      c.name.toLowerCase().includes(suggestedName) || 
      suggestedName.includes(c.name.toLowerCase())
    )
    return match ? match.id.toString() : ''
  }, [categories, data.suggestion.categoryName])

  const form = useForm<FormValues>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      title: data.merchantName ? `Receipt: ${data.merchantName}` : 'New Receipt',
      amount: data.totalAmount,
      type: CategoryType.EXPENSE,
      categoryId: suggestedCategoryId,
      transactionDate: data.transactionDate,
      paymentMethod: PaymentMethod.CARD,
      merchantName: data.merchantName,
    },
  })

  // Update categoryId when suggestedCategoryId is found
  useEffect(() => {
    if (suggestedCategoryId && !form.getValues('categoryId')) {
      form.setValue('categoryId', suggestedCategoryId)
    }
  }, [suggestedCategoryId, form])

  const handleSubmit = async (values: FormValues) => {
    await onSubmit({
      ...values,
      categoryId: Number(values.categoryId),
      description: 'Extracted from receipt scan',
    })
  }

  const getConfidenceColor = (score: number) => {
    if (score > 0.8) return 'bg-emerald-500/10 text-emerald-600 border-emerald-200'
    if (score > 0.5) return 'bg-amber-500/10 text-amber-600 border-amber-200'
    return 'bg-rose-500/10 text-rose-600 border-rose-200'
  }

  return (
    <Card className="border-primary/10 shadow-xl overflow-hidden">
      <CardHeader className="bg-gradient-to-r from-primary/5 to-transparent border-b">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2 text-primary mb-1">
            <Sparkles className="w-5 h-5" />
            <span className="text-sm font-semibold uppercase tracking-wider">Verification Mode</span>
          </div>
          <div className="flex gap-2">
            <Badge variant="outline" className={cn("text-[10px] font-bold px-2 py-0", getConfidenceColor(data.confidence.amount))}>
              AMT {Math.round(data.confidence.amount * 100)}%
            </Badge>
            <Badge variant="outline" className={cn("text-[10px] font-bold px-2 py-0", getConfidenceColor(data.confidence.merchant))}>
              MERCH {Math.round(data.confidence.merchant * 100)}%
            </Badge>
          </div>
        </div>
        <CardTitle className="text-2xl mt-2">Review & Correct</CardTitle>
        <CardDescription>
          Compare the extracted fields with the receipt image on the left. Edit any field to correct it.
        </CardDescription>
      </CardHeader>
      
      <CardContent className="pt-6">
        <Form {...form}>
          <form onSubmit={form.handleSubmit(handleSubmit)} className="space-y-6">
            <div className="grid grid-cols-1 xl:grid-cols-2 gap-x-8 gap-y-6">
              <FormField
                control={form.control}
                name="merchantName"
                render={({ field }) => (
                  <FormItem>
                    <div className="flex items-center justify-between">
                      <FormLabel>Merchant</FormLabel>
                      <span className="text-[10px] text-muted-foreground">Confidence: {Math.round(data.confidence.merchant * 100)}%</span>
                    </div>
                    <FormControl>
                      <Input placeholder="Merchant Name" {...field} className="focus:ring-primary" />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />

              <FormField
                control={form.control}
                name="amount"
                render={({ field }) => (
                  <FormItem>
                    <div className="flex items-center justify-between">
                      <FormLabel>Total Amount</FormLabel>
                      <span className="text-[10px] text-muted-foreground">Confidence: {Math.round(data.confidence.amount * 100)}%</span>
                    </div>
                    <FormControl>
                      <div className="relative">
                        <span className="absolute left-3 top-1/2 -translate-y-1/2 text-muted-foreground font-medium">$</span>
                        <Input type="number" step="0.01" className="pl-7 text-lg font-semibold" placeholder="0.00" {...field} />
                      </div>
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />

              <FormField
                control={form.control}
                name="transactionDate"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Date</FormLabel>
                    <FormControl>
                      <Input type="date" {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />

              <FormField
                control={form.control}
                name="categoryId"
                render={({ field }) => (
                  <FormItem>
                    <div className="flex items-center justify-between">
                      <FormLabel>Category</FormLabel>
                      {data.suggestion.confidence > 0.3 && (
                        <span className={cn(
                          "text-[10px] font-medium flex items-center gap-1",
                          data.suggestion.isAiEnhanced ? "text-indigo-600" : "text-emerald-600"
                        )}>
                          <Sparkles className={cn("w-2.5 h-2.5", data.suggestion.isAiEnhanced && "animate-pulse")} />
                          {data.suggestion.isAiEnhanced ? 'AI Suggested:' : 'Matched:'} {data.suggestion.categoryName}
                        </span>
                      )}
                    </div>
                    <FormControl>
                      <CategoryDropdown
                        value={field.value}
                        onChange={field.onChange}
                        typeFilter={CategoryType.EXPENSE}
                        placeholder={suggestedCategoryId ? 'Using suggestion...' : 'Select a category'}
                      />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
            </div>

            <FormField
              control={form.control}
              name="title"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Transaction Title</FormLabel>
                  <FormControl>
                    <Input placeholder="e.g. Lunch at Cafe" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <div className="bg-muted/30 rounded-lg p-4 border border-dashed flex gap-3">
              <Info className="w-5 h-5 text-muted-foreground shrink-0 mt-0.5" />
              <div className="text-xs text-muted-foreground leading-relaxed">
                <p className="font-semibold text-foreground mb-1">OCR Insight</p>
                Parsed from receipt text. The confidence scores indicate our certainty about each field. Always verify amounts before saving.
              </div>
            </div>

            <div className="flex justify-end gap-3 pt-6 border-t">
              <Button type="button" variant="ghost" onClick={onCancel} disabled={loading}>
                Discard Scan
              </Button>
              <Button type="submit" disabled={loading} className="px-10 bg-primary hover:bg-primary/90">
                {loading ? 'Saving...' : 'Confirm & Save'}
              </Button>
            </div>
          </form>
        </Form>
      </CardContent>
    </Card>
  )
}
