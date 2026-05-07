import { Pencil, Trash2 } from 'lucide-react'

import { Button } from '@/components/ui/button'
import { CategoryType, type Transaction } from '@/types'

interface TransactionTableProps {
  transactions: Transaction[]
  loading: boolean
  onEdit: (transaction: Transaction) => void
  onDelete: (id: string | number) => void
}

export function TransactionTable({ transactions, loading, onEdit, onDelete }: TransactionTableProps) {
  if (loading && transactions.length === 0) {
    return (
      <div className="bg-white dark:bg-zinc-900 rounded-lg border shadow-sm p-8 text-center text-gray-500">
        Loading transactions...
      </div>
    )
  }

  return (
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
                      onClick={() => onEdit(t)}
                      title="Edit"
                    >
                      <Pencil className="w-4 h-4" />
                    </Button>
                    <Button
                      variant="ghost"
                      className="w-8 h-8 p-0 text-red-500 hover:text-red-600 hover:bg-red-50 dark:hover:bg-red-950"
                      onClick={() => onDelete(t.id)}
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
                  No transactions found. Try adjusting your filters or add a new one.
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  )
}
