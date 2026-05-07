import { Plus, Pencil, Trash2 } from 'lucide-react'
import { useEffect, useState } from 'react'

import { TransactionForm } from '@/components/TransactionForm'
import { Button } from '@/components/ui/button'
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from '@/components/ui/dialog'
import { useTransactionStore } from '@/store/transaction.store'
import { Transaction, CategoryType, CreateTransactionRequest } from '@/types'

export default function TransactionsPage() {
  const { transactions, loading, fetchTransactions, createTransaction, updateTransaction, deleteTransaction, pagination } = useTransactionStore()
  const [isDialogOpen, setIsDialogOpen] = useState(false)
  const [editingTransaction, setEditingTransaction] = useState<Transaction | null>(null)

  useEffect(() => {
    fetchTransactions()
  }, [fetchTransactions])

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

      <div className="bg-white dark:bg-zinc-900 rounded-lg border shadow-sm overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-sm text-left">
            <thead className="bg-gray-50 dark:bg-zinc-800 border-b">
              <tr>
                <th className="px-6 py-3 font-medium">Date</th>
                <th className="px-6 py-3 font-medium">Title</th>
                <th className="px-6 py-3 font-medium">Category</th>
                <th className="px-6 py-3 font-medium text-right">Amount</th>
                <th className="px-6 py-3 font-medium text-right">Actions</th>
              </tr>
            </thead>
            <tbody>
              {transactions.map((t) => (
                <tr key={t.id} className="border-b last:border-0 hover:bg-gray-50 dark:hover:bg-zinc-800/50">
                  <td className="px-6 py-4 whitespace-nowrap text-gray-500">
                    {new Date(t.transactionDate).toLocaleDateString()}
                  </td>
                  <td className="px-6 py-4">
                    <div className="font-medium text-gray-900 dark:text-gray-100">{t.title}</div>
                    {t.merchantName && (
                      <div className="text-xs text-gray-500">{t.merchantName}</div>
                    )}
                  </td>
                  <td className="px-6 py-4">
                    <div className="flex items-center gap-2">
                      {t.categoryColor && (
                        <div
                          className="w-3 h-3 rounded-full shrink-0"
                          style={{ backgroundColor: t.categoryColor }}
                        />
                      )}
                      <span>{t.categoryName}</span>
                    </div>
                  </td>
                  <td className="px-6 py-4 text-right">
                    <span className={`font-semibold ${
                      t.type === CategoryType.INCOME ? 'text-green-600 dark:text-green-400' : 'text-gray-900 dark:text-gray-100'
                    }`}>
                      {t.type === CategoryType.INCOME ? '+' : '-'}${t.amount.toFixed(2)}
                    </span>
                  </td>
                  <td className="px-6 py-4 text-right">
                    <div className="flex items-center justify-end gap-2">
                      <Button
                        variant="ghost"
                        className="w-8 h-8 p-0"
                        onClick={() => handleEdit(t)}
                        title="Edit"
                      >
                        <Pencil className="w-4 h-4" />
                      </Button>
                      <Button
                        variant="ghost"
                        className="w-8 h-8 p-0 text-red-500 hover:text-red-600 hover:bg-red-50 dark:hover:bg-red-950"
                        onClick={() => handleDelete(t.id)}
                        title="Delete"
                      >
                        <Trash2 className="w-4 h-4" />
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
              {transactions.length === 0 && !loading && (
                <tr>
                  <td colSpan={5} className="px-6 py-8 text-center text-gray-500">
                    No transactions found. Start by adding one.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>
      
      {transactions.length > 0 && (
        <div className="flex items-center justify-between mt-4 text-sm text-gray-500">
          <div>
            Showing {transactions.length} of {pagination.totalElements} entries
          </div>
          <div className="flex gap-2">
            <Button
              className="bg-zinc-800 text-white dark:bg-white dark:text-zinc-800"
              disabled={pagination.page === 0}
              onClick={() => fetchTransactions({ page: pagination.page - 1 })}
            >
              Previous
            </Button>
            <Button
              className="bg-zinc-800 text-white dark:bg-white dark:text-zinc-800"
              disabled={pagination.page >= pagination.totalPages - 1}
              onClick={() => fetchTransactions({ page: pagination.page + 1 })}
            >
              Next
            </Button>
          </div>
        </div>
      )}
    </div>
  )
}
