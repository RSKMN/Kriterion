import { Edit2, MoreVertical, Trash2 } from 'lucide-react'

import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu'
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table'
import { RecurringTransaction } from '@/types'

interface RecurringTransactionTableProps {
  data: RecurringTransaction[]
  onEdit: (rt: RecurringTransaction) => void
  onDelete: (id: string | number) => void
}

const formatCurrency = (amount: number) => {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD',
  }).format(amount)
}

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleDateString('en-US', {
    month: 'short',
    day: 'numeric',
    year: 'numeric',
  })
}

export function RecurringTransactionTable({
  data,
  onEdit,
  onDelete,
}: RecurringTransactionTableProps) {
  return (
    <div className="rounded-md border bg-card">
      <Table>
        <TableHeader>
          <TableRow>
            <TableHead>Title</TableHead>
            <TableHead>Category</TableHead>
            <TableHead>Amount</TableHead>
            <TableHead>Recurrence</TableHead>
            <TableHead>Next Run</TableHead>
            <TableHead>Status</TableHead>
            <TableHead className="w-[50px]"></TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {data.length === 0 ? (
            <TableRow>
              <TableCell colSpan={7} className="h-24 text-center text-muted-foreground">
                No recurring transactions found.
              </TableCell>
            </TableRow>
          ) : (
            data.map((rt) => (
              <TableRow key={rt.id}>
                <TableCell className="font-medium">{rt.title}</TableCell>
                <TableCell>{rt.categoryName}</TableCell>
                <TableCell className={rt.type === 'INCOME' ? 'text-emerald-600' : 'text-rose-600'}>
                  {rt.type === 'INCOME' ? '+' : '-'} {formatCurrency(rt.amount)}
                </TableCell>
                <TableCell>
                  <Badge variant="secondary" className="font-normal">
                    {rt.recurrenceType.toLowerCase()}
                  </Badge>
                </TableCell>
                <TableCell>{formatDate(rt.nextRunDate)}</TableCell>
                <TableCell>
                  <Badge variant={rt.isActive ? 'default' : 'outline'} className={rt.isActive ? 'bg-emerald-500/10 text-emerald-600 border-none' : ''}>
                    {rt.isActive ? 'Active' : 'Inactive'}
                  </Badge>
                </TableCell>
                <TableCell>
                  <DropdownMenu>
                    <DropdownMenuTrigger asChild>
                      <Button variant="ghost" size="icon" className="h-8 w-8">
                        <MoreVertical className="h-4 w-4" />
                      </Button>
                    </DropdownMenuTrigger>
                    <DropdownMenuContent align="end">
                      <DropdownMenuItem onClick={() => onEdit(rt)}>
                        <Edit2 className="mr-2 h-4 w-4" />
                        Edit
                      </DropdownMenuItem>
                      <DropdownMenuItem 
                        className="text-destructive focus:text-destructive"
                        onClick={() => onDelete(rt.id)}
                      >
                        <Trash2 className="mr-2 h-4 w-4" />
                        Delete
                      </DropdownMenuItem>
                    </DropdownMenuContent>
                  </DropdownMenu>
                </TableCell>
              </TableRow>
            ))
          )}
        </TableBody>
      </Table>
    </div>
  )
}
