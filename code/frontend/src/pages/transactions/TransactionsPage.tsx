import { Plus } from 'lucide-react'
import { useEffect, useState } from 'react'

import { TransactionFilterBar, TransactionFilters } from '@/components/TransactionFilterBar'
import { TransactionForm } from '@/components/TransactionForm'
import { TransactionTable } from '@/components/TransactionTable'
import { Button } from '@/components/ui/button'
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from '@/components/ui/dialog'
import { useTransactionStore } from '@/store/transaction.store'
import { CreateTransactionRequest, Transaction } from '@/types'

export default function TransactionsPage() {
  const { transactions, loading, fetchTransactions, createTransaction, updateTransaction, deleteTransaction, pagination } = useTransactionStore()
  const [isDialogOpen, setIsDialogOpen] = useState(false)
  const [editingTransaction, setEditingTransaction] = useState<Transaction | null>(null)
  const [filters, setFilters] = useState<TransactionFilters>({})

  // Initial fetch
  useEffect(() => {
    fetchTransactions()
  }, [fetchTransactions])

  const handleFilterChange = (newFilters: TransactionFilters) => {
    setFilters(newFilters)
    fetchTransactions({ ...newFilters, page: 0 }) 
  }

  const handleOpenChange = (open: boolean) => {
    setIsDialogOpen(open)
    if (!open) {
      setEditingTransaction(null)
    }
  }

  const handleEdit = (transaction: Transaction) => {
    setEditingTransaction(transaction)
    setIsDialogOpen(true)
  }

  const handleDelete = async (id: string | number) => {
    if (confirm('Are you sure you want to delete this transaction?')) {
      await deleteTransaction(id)
    }
  }

  const handleSubmit = async (data: CreateTransactionRequest) => {
    if (editingTransaction) {
      await updateTransaction(editingTransaction.id, data)
    } else {
      await createTransaction(data)
    }
    setIsDialogOpen(false)
    setEditingTransaction(null)
  }

  return (
    <div className="container mx-auto py-8 px-4 max-w-6xl">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold">Transactions</h1>
        <Dialog open={isDialogOpen} onOpenChange={handleOpenChange}>
          <DialogTrigger asChild>
            <Button>
              <Plus className="w-4 h-4 mr-2" />
              Add Transaction
            </Button>
          </DialogTrigger>
          <DialogContent className="sm:max-w-2xl">
            <DialogHeader>
              <DialogTitle>{editingTransaction ? 'Edit Transaction' : 'Create Transaction'}</DialogTitle>
            </DialogHeader>
            <TransactionForm
              initialData={editingTransaction}
              onSubmit={handleSubmit}
              onCancel={() => handleOpenChange(false)}
              loading={loading}
            />
          </DialogContent>
        </Dialog>
      </div>

      <TransactionFilterBar 
        onFilterChange={handleFilterChange} 
        initialFilters={filters}
      />

      <TransactionTable
        transactions={transactions}
        loading={loading}
        onEdit={handleEdit}
        onDelete={handleDelete}
      />
      
      {transactions.length > 0 && (
        <div className="flex items-center justify-between mt-4 text-sm text-gray-500">
          <div>
            Showing {transactions.length} of {pagination.totalElements} entries
          </div>
          <div className="flex gap-2">
            <Button
              className="bg-zinc-800 text-white dark:bg-white dark:text-zinc-800"
              disabled={pagination.page === 0}
              onClick={() => fetchTransactions({ ...filters, page: pagination.page - 1 })}
            >
              Previous
            </Button>
            <Button
              className="bg-zinc-800 text-white dark:bg-white dark:text-zinc-800"
              disabled={pagination.page >= pagination.totalPages - 1}
              onClick={() => fetchTransactions({ ...filters, page: pagination.page + 1 })}
            >
              Next
            </Button>
          </div>
        </div>
      )}
    </div>
  )
}
