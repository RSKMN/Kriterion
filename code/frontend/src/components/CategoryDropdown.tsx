import { useEffect, useMemo } from 'react'

import {
  Select,
  SelectContent,
  SelectGroup,
  SelectItem,
  SelectLabel,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import { useCategoryStore } from '@/store/category.store'
import { CategoryType } from '@/types'

interface CategoryDropdownProps {
  value?: string
  onChange: (value: string) => void
  typeFilter?: CategoryType
  placeholder?: string
  disabled?: boolean
  showOverallOption?: boolean
}

export function CategoryDropdown({
  value,
  onChange,
  typeFilter,
  placeholder = 'Select a category',
  disabled = false,
  showOverallOption = false,
}: CategoryDropdownProps) {
  const { categories, fetchCategories, loading } = useCategoryStore()

  useEffect(() => {
    if (categories.length === 0) {
      fetchCategories()
    }
  }, [categories.length, fetchCategories])

  const filteredCategories = useMemo(() => {
    let filtered = categories
    if (typeFilter) {
      filtered = filtered.filter((c) => c.type === typeFilter)
    }
    return filtered
  }, [categories, typeFilter])

  const defaultCategories = filteredCategories.filter((c) => c.isDefault)
  const customCategories = filteredCategories.filter((c) => !c.isDefault)

  return (
    <Select value={value} onValueChange={onChange} disabled={disabled || loading}>
      <SelectTrigger className="w-full">
        <SelectValue placeholder={loading ? 'Loading...' : placeholder} />
      </SelectTrigger>
      <SelectContent>
        {showOverallOption && (
          <SelectGroup>
            <SelectItem value="overall">Overall Budget</SelectItem>
          </SelectGroup>
        )}
        
        {defaultCategories.length > 0 && (
          <SelectGroup>
            <SelectLabel>System Categories</SelectLabel>
            {defaultCategories.map((cat) => (
              <SelectItem key={cat.id} value={cat.id.toString()}>
                <div className="flex items-center gap-2">
                  {cat.color && (
                    <div
                      className="h-3 w-3 rounded-full shrink-0"
                      style={{ backgroundColor: cat.color }}
                    />
                  )}
                  <span>{cat.name}</span>
                </div>
              </SelectItem>
            ))}
          </SelectGroup>
        )}
        
        {customCategories.length > 0 && (
          <SelectGroup>
            <SelectLabel>My Categories</SelectLabel>
            {customCategories.map((cat) => (
              <SelectItem key={cat.id} value={cat.id.toString()}>
                <div className="flex items-center gap-2">
                  {cat.color && (
                    <div
                      className="h-3 w-3 rounded-full shrink-0"
                      style={{ backgroundColor: cat.color }}
                    />
                  )}
                  <span>{cat.name}</span>
                </div>
              </SelectItem>
            ))}
          </SelectGroup>
        )}
        
        {filteredCategories.length === 0 && !loading && (
          <div className="p-2 text-sm text-gray-500 text-center">
            No categories found.
          </div>
        )}
      </SelectContent>
    </Select>
  )
}
