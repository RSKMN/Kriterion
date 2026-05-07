import { Search, X } from 'lucide-react'
import { useEffect, useState } from 'react'

import { CategoryDropdown } from '@/components/CategoryDropdown'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import { CategoryType } from '@/types'

export interface TransactionFilters {
  search?: string
  categoryId?: string
  type?: string
  startDate?: string
  endDate?: string
}

interface TransactionFilterBarProps {
  onFilterChange: (filters: TransactionFilters) => void
  initialFilters?: TransactionFilters
}

export function TransactionFilterBar({ onFilterChange, initialFilters }: TransactionFilterBarProps) {
  const [filters, setFilters] = useState<TransactionFilters>(initialFilters || {})
  const [searchTerm, setSearchTerm] = useState(initialFilters?.search || '')

  useEffect(() => {
    const timer = setTimeout(() => {
      setFilters((prev) => {
        if (prev.search === (searchTerm || undefined)) return prev
        const next = { ...prev, search: searchTerm || undefined }
        onFilterChange(next)
        return next
      })
    }, 500)
    return () => clearTimeout(timer)
  }, [searchTerm, onFilterChange])

  const handleFilterChange = (key: keyof TransactionFilters, value: any) => {
    const newFilters = { ...filters, [key]: value || undefined }
    setFilters(newFilters)
    onFilterChange(newFilters)
  }

  const handleClearFilters = () => {
    setSearchTerm('')
    setFilters({})
    onFilterChange({})
  }

  const hasActiveFilters = Object.values(filters).some((val) => val !== undefined && val !== '')

  return (
    <div className="bg-white dark:bg-zinc-900 p-4 rounded-lg border shadow-sm space-y-4 mb-6">
      <div className="flex justify-between items-center mb-2">
        <h3 className="text-sm font-medium text-gray-700 dark:text-gray-300">Filters</h3>
        {hasActiveFilters && (
          <Button variant="ghost" onClick={handleClearFilters} className="h-8 px-2 text-xs text-gray-500 hover:text-gray-900">
            <X className="w-3 h-3 mr-1" /> Clear
          </Button>
        )}
      </div>
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-6 gap-4">
        {/* Search */}
        <div className="relative lg:col-span-2">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-gray-500" />
          <Input
            placeholder="Search by title or merchant..."
            className="pl-9"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
        </div>

        {/* Type Filter */}
        <div className="lg:col-span-1">
          <Select
            value={filters.type || 'ALL'}
            onValueChange={(val) => {
              const newType = val === 'ALL' ? undefined : val
              const newFilters = { ...filters, type: newType, categoryId: undefined }
              setFilters(newFilters)
              onFilterChange(newFilters)
            }}
          >
            <SelectTrigger>
              <SelectValue placeholder="All Types" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="ALL">All Types</SelectItem>
              <SelectItem value={CategoryType.EXPENSE}>Expense</SelectItem>
              <SelectItem value={CategoryType.INCOME}>Income</SelectItem>
            </SelectContent>
          </Select>
        </div>

        {/* Category Filter */}
        <div className="lg:col-span-1">
          <CategoryDropdown
            value={filters.categoryId}
            onChange={(val) => handleFilterChange('categoryId', val)}
            typeFilter={filters.type as CategoryType}
            placeholder="All Categories"
          />
        </div>

        {/* Date Filter */}
        <div className="flex gap-2 lg:col-span-2">
          <div className="w-1/2">
            <Input
              type="date"
              placeholder="Start Date"
              value={filters.startDate || ''}
              onChange={(e) => handleFilterChange('startDate', e.target.value)}
              title="Start Date"
            />
          </div>
          <div className="w-1/2">
            <Input
              type="date"
              placeholder="End Date"
              value={filters.endDate || ''}
              onChange={(e) => handleFilterChange('endDate', e.target.value)}
              title="End Date"
            />
          </div>
        </div>
      </div>
    </div>
  )
}
