import { zodResolver } from '@hookform/resolvers/zod'
import { Plus, Pencil, Trash2 } from 'lucide-react'
import { useEffect, useState } from 'react'
import { useForm } from 'react-hook-form'
import * as z from 'zod'

import { Button } from '@/components/ui/button'
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
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
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import { useCategoryStore } from '@/store/category.store'
import { Category, CategoryType } from '@/types'

const formSchema = z.object({
  name: z.string().min(1, 'Name is required').max(100),
  type: z.nativeEnum(CategoryType),
  color: z.string().max(20).optional(),
  icon: z.string().max(100).optional(),
})

type FormValues = z.infer<typeof formSchema>

export function CategoriesPage() {
  const { categories, loading, fetchCategories, createCategory, updateCategory, deleteCategory } = useCategoryStore()
  const [isDialogOpen, setIsDialogOpen] = useState(false)
  const [editingCategory, setEditingCategory] = useState<Category | null>(null)

  const form = useForm<FormValues>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      name: '',
      type: CategoryType.EXPENSE,
      color: '#000000',
    },
  })

  useEffect(() => {
    fetchCategories()
  }, [fetchCategories])

  const onSubmit = async (values: FormValues) => {
    try {
      if (editingCategory) {
        await updateCategory(editingCategory.id, {
          name: values.name,
          color: values.color,
          icon: values.icon,
        })
      } else {
        await createCategory(values)
      }
      setIsDialogOpen(false)
      form.reset()
      setEditingCategory(null)
    } catch {
      // Error is handled in store
    }
  }

  const handleEdit = (category: Category) => {
    setEditingCategory(category)
    form.reset({
      name: category.name,
      type: category.type,
      color: category.color || '#000000',
      icon: category.icon || '',
    })
    setIsDialogOpen(true)
  }

  const handleDelete = async (id: string | number) => {
    if (confirm('Are you sure you want to delete this category?')) {
      await deleteCategory(id)
    }
  }

  const handleOpenChange = (open: boolean) => {
    setIsDialogOpen(open)
    if (!open) {
      form.reset()
      setEditingCategory(null)
    }
  }

  return (
    <div className="container mx-auto py-8 px-4 max-w-5xl">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold">Categories</h1>
        <Dialog open={isDialogOpen} onOpenChange={handleOpenChange}>
          <DialogTrigger asChild>
            <Button>
              <Plus className="w-4 h-4 mr-2" />
              Add Category
            </Button>
          </DialogTrigger>
          <DialogContent className="sm:max-w-md">
            <DialogHeader>
              <DialogTitle>{editingCategory ? 'Edit Category' : 'Create Category'}</DialogTitle>
            </DialogHeader>
            <Form {...form}>
              <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
                <FormField
                  control={form.control}
                  name="name"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel>Name</FormLabel>
                      <FormControl>
                        <Input placeholder="Groceries" {...field} />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />

                <FormField
                  control={form.control}
                  name="type"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel>Type</FormLabel>
                      <Select
                        disabled={!!editingCategory} // Cannot change type after creation
                        onValueChange={field.onChange}
                        defaultValue={field.value}
                      >
                        <FormControl>
                          <SelectTrigger>
                            <SelectValue placeholder="Select type" />
                          </SelectTrigger>
                        </FormControl>
                        <SelectContent>
                          <SelectItem value={CategoryType.EXPENSE}>Expense</SelectItem>
                          <SelectItem value={CategoryType.INCOME}>Income</SelectItem>
                        </SelectContent>
                      </Select>
                      <FormMessage />
                    </FormItem>
                  )}
                />

                <FormField
                  control={form.control}
                  name="color"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel>Color</FormLabel>
                      <FormControl>
                        <div className="flex items-center gap-2">
                          <Input type="color" className="w-12 h-10 p-1 cursor-pointer" {...field} />
                          <Input placeholder="#000000" {...field} />
                        </div>
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />

                <div className="flex justify-end pt-4">
                  <Button type="submit" disabled={form.formState.isSubmitting || loading}>
                    {form.formState.isSubmitting || loading ? 'Saving...' : 'Save'}
                  </Button>
                </div>
              </form>
            </Form>
          </DialogContent>
        </Dialog>
      </div>

      {loading && categories.length === 0 ? (
        <div className="py-12 flex justify-center">Loading categories...</div>
      ) : (
        <div className="bg-white dark:bg-zinc-900 rounded-lg border shadow-sm overflow-hidden">
          <table className="w-full text-sm text-left">
            <thead className="bg-gray-50 dark:bg-zinc-800 border-b">
              <tr>
                <th className="px-6 py-3 font-medium">Name</th>
                <th className="px-6 py-3 font-medium">Type</th>
                <th className="px-6 py-3 font-medium text-right">Actions</th>
              </tr>
            </thead>
            <tbody>
              {categories.map((category) => (
                <tr key={category.id} className="border-b last:border-0 hover:bg-gray-50 dark:hover:bg-zinc-800/50">
                  <td className="px-6 py-4">
                    <div className="flex items-center gap-3">
                      {category.color && (
                        <div
                          className="w-4 h-4 rounded-full shrink-0"
                          style={{ backgroundColor: category.color }}
                        />
                      )}
                      <span className="font-medium">
                        {category.name}
                        {category.isDefault && (
                          <span className="ml-2 text-xs bg-gray-100 dark:bg-zinc-700 px-2 py-0.5 rounded text-gray-600 dark:text-gray-300 font-normal">
                            System
                          </span>
                        )}
                      </span>
                    </div>
                  </td>
                  <td className="px-6 py-4">
                    <span className={`inline-flex items-center px-2 py-0.5 rounded text-xs font-medium ${
                      category.type === CategoryType.INCOME 
                        ? 'bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-400' 
                        : 'bg-red-100 text-red-800 dark:bg-red-900/30 dark:text-red-400'
                    }`}>
                      {category.type}
                    </span>
                  </td>
                  <td className="px-6 py-4 text-right">
                    {!category.isDefault && (
                      <div className="flex items-center justify-end gap-2">
                        <Button
                          variant="ghost"
                          className="w-8 h-8 p-0"
                          onClick={() => handleEdit(category)}
                          title="Edit"
                        >
                          <Pencil className="w-4 h-4" />
                        </Button>
                        <Button
                          variant="ghost"
                          className="w-8 h-8 p-0 text-red-500 hover:text-red-600 hover:bg-red-50 dark:hover:bg-red-950"
                          onClick={() => handleDelete(category.id)}
                          title="Delete"
                        >
                          <Trash2 className="w-4 h-4" />
                        </Button>
                      </div>
                    )}
                  </td>
                </tr>
              ))}
              {categories.length === 0 && (
                <tr>
                  <td colSpan={3} className="px-6 py-8 text-center text-gray-500">
                    No categories found. Start by adding one.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      )}
    </div>
  )
}
